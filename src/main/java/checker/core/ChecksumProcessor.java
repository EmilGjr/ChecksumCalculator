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

// Walk the file structure and compute checksums.
// Send progress updates through ProgressReporter.
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

    // Start processing
    public Map<String, String> process(FileSystemNode node) {
        return process(node, null);
    }

    public Map<String, String> process(FileSystemNode node, ScanMemento resumeState) {
        Map<String, String> results = new LinkedHashMap<>();
        completedChecksums.clear();
        if (resumeState != null) {
            completedChecksums.putAll(resumeState.getCompletedChecksums());
            processedFiles = resumeState.getProcessedFiles();
            totalBytesRead.set(resumeState.getTotalBytesProcessed());
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
        rootPath = node.getPath();
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
                completedChecksums);
    }

    public void restoreMemento(ScanState state) {
        this.totalFiles = state.getTotalFiles();
        this.processedFiles = state.getProcessedFiles();
        this.totalBytes = state.getTotalBytes();
        this.totalBytesRead.set(state.getTotalBytesProcessed());
    }

    public static class ScanState {
        private final int totalFiles;
        private final int processedFiles;
        private final long totalBytes;
        private final long totalBytesProcessed;

        public ScanState(int totalFiles,
                         int processedFiles,
                         long totalBytes,
                         long totalBytesProcessed) {
            this.totalFiles = totalFiles;
            this.processedFiles = processedFiles;
            this.totalBytes = totalBytes;
            this.totalBytesProcessed = totalBytesProcessed;
        }

        public int getTotalFiles() {
            return totalFiles;
        }

        public int getProcessedFiles() {
            return processedFiles;
        }

        public long getTotalBytes() {
            return totalBytes;
        }

        public long getTotalBytesProcessed() {
            return totalBytesProcessed;
        }
    }

    // Count all files (for progress percentage)
    private int countFiles(FileSystemNode node) {
        if (node instanceof FileNode) {
            return 1;
        }

        int count = 0;
        if (node instanceof DirectoryNode) {
            DirectoryNode dir = (DirectoryNode) node;
            for (FileSystemNode child : dir.getChildren()) {
                count += countFiles(child);
            }
        }
        return count;
    }

    // Real processing
    private void processNode(FileSystemNode node,
                             Map<String,String> results,
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
            DirectoryNode dir = (DirectoryNode) node;
            for (FileSystemNode child : dir.getChildren()) {
                processNode(child, results, rootPath);
                if (controller.isStopRequested()) {
                    break;
                }
            }
        }
    }

    private void processFile(FileNode file,
                             Map<String,String> results,
                             Path rootPath) {
        Path filePath = file.getPath();
        String relativePath = getRelativePath(filePath, rootPath);

        if (completedChecksums.containsKey(relativePath)) {
            return;
        }

        currentFile = relativePath;
        currentFileBytes = 0;
        AtomicLong fileBytesRead = new AtomicLong(0);

        try (InputStream rawStream = new FileInputStream(filePath.toFile());
             ProgressInputStream progressStream = new ProgressInputStream(rawStream, bytes -> {
                 fileBytesRead.addAndGet(bytes);
                 currentFileBytes = fileBytesRead.get();
                 long overall = totalBytesRead.addAndGet(bytes);
                 reporter.notifyObservers(new ProgressMessage(
                         relativePath,
                         processedFiles,
                         totalFiles,
                         fileBytesRead.get(),
                         file.getSize(),
                         overall,
                         totalBytes));
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
            reporter.notifyObservers(new ProgressMessage(
                    relativePath,
                    processedFiles,
                    totalFiles,
                    file.getSize(),
                    file.getSize(),
                    totalBytesRead.get(),
                    totalBytes));
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                System.err.println("Error reading " + relativePath + ": " + e.getCause().getMessage());
            } else {
                throw e;
            }
        } catch (IOException e) {
            System.err.println("Error reading " + relativePath + ": " + e.getMessage());
        }
    }

    private String getRelativePath(Path filePath, Path rootPath) {
        if (filePath == null) {
            return "";
        }
        if (rootPath == null) {
            return normalizePath(filePath.toString());
        }
        if (filePath.equals(rootPath)) {
            return normalizePath(filePath.getFileName().toString());
        }
        try {
            String relative = rootPath.relativize(filePath).toString();
            if (relative.isBlank()) {
                return normalizePath(filePath.getFileName().toString());
            }
            return normalizePath(relative);
        } catch (IllegalArgumentException e) {
            return normalizePath(filePath.toString());
        }
    }

    private String normalizePath(String path) {
        return path.replace('\\', '/');
    }
}
