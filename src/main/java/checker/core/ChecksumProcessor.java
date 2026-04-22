/**
 * This class manages the process of scanning files to calculate checksums.
 * It walks directories, computes hashes, reports progress, and supports pause/resume.
 */
package checker.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import checker.control.ProgressInputStream;
import checker.control.ScanController;
import checker.filesystem.DirectoryNode;
import checker.filesystem.FileNode;
import checker.filesystem.FileSystemNode;
import checker.storage.ScanMemento;
import progress.ProgressMessage;
import progress.ProgressReporter;

public class ChecksumProcessor {

    private final ChecksumCalculator calculator;
    private final ProgressReporter reporter;
    private final ScanController controller;

    private int totalFiles;
    private int processedFiles;
    private long totalBytes;

    private final AtomicLong totalBytesRead = new AtomicLong(0);

    private String currentFile;
    private long currentFileBytes;

    private final Map<String, String> completedChecksums = new LinkedHashMap<>();

    private Path rootPath;

    public ChecksumProcessor(ChecksumCalculator calculator, ProgressReporter reporter) {
        this(calculator, reporter, new ScanController());
    }

    public ChecksumProcessor(ChecksumCalculator calculator,
                             ProgressReporter reporter,
                             ScanController controller) {
        this.calculator = calculator;
        this.reporter = reporter;
        this.controller = controller;
    }

    public Map<String, String> process(FileSystemNode node) {
        return process(node, null);
    }

    public Map<String, String> process(FileSystemNode node, ScanMemento resumeState) {

        Map<String, String> results = new LinkedHashMap<>();

        completedChecksums.clear();

        if (resumeState != null) {
            completedChecksums.putAll(resumeState.getCompletedChecksums());
            processedFiles = resumeState.getProcessedFiles();
            long resumedBytes = Math.max(
                    0,
                    resumeState.getTotalBytesProcessed() - resumeState.getCurrentFileBytes()
            );
            totalBytesRead.set(resumedBytes);
            currentFile = resumeState.getCurrentFile();
            currentFileBytes = resumeState.getCurrentFileBytes();
        } else {
            processedFiles = 0;
            totalBytesRead.set(0);
            currentFile = null;
            currentFileBytes = 0;
        }

        totalFiles = countFiles(node);
        totalBytes = node.getSize();

        rootPath = node.getPath().toAbsolutePath().normalize();

        results.putAll(completedChecksums);

        processNode(node, results, rootPath);

        return results;
    }

    public ScanMemento createMemento(String algorithm, boolean followLinks) {
        return new ScanMemento(
                rootPath == null ? "" : rootPath.toString(),
                algorithm,
                followLinks,
                totalFiles,
                processedFiles,
                totalBytes,
                totalBytesRead.get(),
                currentFile,
                currentFileBytes,
                completedChecksums
        );
    }

    private int countFiles(FileSystemNode node) {
        if (node instanceof FileNode) {
            return 1;
        }

        int count = 0;
        if (node instanceof DirectoryNode) {
            for (FileSystemNode child : ((DirectoryNode) node).getChildren()) {
                count += countFiles(child);
            }
        }
        return count;
    }

    private void processNode(FileSystemNode node,
                             Map<String, String> results,
                             Path rootPath) {

        controller.waitIfPaused();

        if (controller.isStopRequested()) {
            return;
        }

        if (node instanceof FileNode) {
            processFile((FileNode) node, results, rootPath);
            return;
        }

        if (node instanceof DirectoryNode) {
            for (FileSystemNode child : ((DirectoryNode) node).getChildren()) {
                processNode(child, results, rootPath);

                if (controller.isStopRequested()) {
                    break;
                }
            }
        }
    }

    private void processFile(FileNode fileNode,
                             Map<String, String> results,
                             Path rootPath) {

        Path filePath = fileNode.getPath();

        Path root = rootPath.toAbsolutePath().normalize();
        Path normalizedFilePath = filePath.toAbsolutePath().normalize();

        String relativePath = normalize(
        root.relativize(normalizedFilePath).toString());

        if (completedChecksums.containsKey(relativePath)) {
            return;
        }

        currentFile = relativePath;
        currentFileBytes = 0;

        AtomicLong fileBytesRead = new AtomicLong(0);

        try (InputStream rawStream = new FileInputStream(filePath.toFile());
             ProgressInputStream progressStream =
                     new ProgressInputStream(rawStream, bytes -> {

                         fileBytesRead.addAndGet(bytes);
                         currentFileBytes = fileBytesRead.get();

                         long overall = totalBytesRead.addAndGet(bytes);

                         reporter.notifyObservers(new ProgressMessage(
                                 relativePath,
                                 processedFiles,
                                 totalFiles,
                                 fileBytesRead.get(),
                                 fileNode.getSize(),
                                 overall,
                                 totalBytes
                         ));

                         controller.waitIfPaused();

                         if (controller.isStopRequested()) {
                             throw new RuntimeException(new IOException("Scan was stopped"));
                         }
                     })) {

            String hash = calculator.calculate(progressStream);

            results.put(relativePath, hash);
            completedChecksums.put(relativePath, hash);

            processedFiles++;
            currentFile = null;
            currentFileBytes = 0;

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to read file: " + relativePath);
        }
    }
    private String normalize(String p) {
    return p.replace('\\', '/')
            .toLowerCase()
            .replaceAll("^\\./", "")
            .replaceAll("/+", "/");
    }
}
