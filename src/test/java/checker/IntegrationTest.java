/**
 * This class contains integration tests for the overall functionality of the application.
 * Tests the interaction between different components.
 */
package checker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import checker.core.ChecksumProcessor;
import checker.core.Md5Calculator;
import checker.core.Sha256Calculator;
import checker.filesystem.DirectoryNode;
import checker.filesystem.FileNode;
import progress.ProgressMessage;
import progress.ProgressReporter;

class IntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    void testFullFlowWithMd5() throws IOException {
        // --- Create test file structure ---
        File file1 = tempDir.resolve("file1.txt").toFile();
        try (FileOutputStream fos = new FileOutputStream(file1)) {
            fos.write("Hello".getBytes()); // 5 bytes
        }

        File file2 = tempDir.resolve("file2.txt").toFile();
        try (FileOutputStream fos = new FileOutputStream(file2)) {
            fos.write("World!".getBytes()); // 6 bytes
        }

        // --- Assemble the file system tree ---
        DirectoryNode rootDir = new DirectoryNode(tempDir);
        rootDir.addChild(new FileNode(file1.toPath(), file1.length()));
        rootDir.addChild(new FileNode(file2.toPath(), file2.length()));

        // --- Create MD5 calculator ---
        Md5Calculator calculator = new Md5Calculator();

        // --- Create ProgressReporter and track progress ---
        ProgressReporter reporter = new ProgressReporter();
        Map<String, ProgressMessage> progressMap = new HashMap<>();
        reporter.addObserver(message -> progressMap.put(message.getFileName(), message));

        // --- Create and start the processor ---
        ChecksumProcessor processor = new ChecksumProcessor(calculator, reporter);
        Map<String, String> results = processor.process(rootDir);

        // --- Check the results ---
        assertEquals(2, results.size(), "Should have calculated 2 files");
        assertEquals(5, file1.length(), "Size of file1.txt is incorrect");
        assertEquals(6, file2.length(), "Size of file2.txt is incorrect");

        // --- Check progress ---
        assertEquals(2, progressMap.size(), "Should have 2 progress notifications");
        assertEquals("file1.txt", progressMap.get("file1.txt").getFileName());
        assertEquals("file2.txt", progressMap.get("file2.txt").getFileName());
    }

    @Test
    void testFullFlowWithSha256() throws IOException {
        // --- Create test file structure ---
        File file1 = tempDir.resolve("abc.txt").toFile();
        try (FileOutputStream fos = new FileOutputStream(file1)) {
            fos.write("abc".getBytes());
        }

        // --- File system tree ---
        DirectoryNode rootDir = new DirectoryNode(tempDir);
        rootDir.addChild(new FileNode(file1.toPath(), file1.length()));

        // --- SHA-256 calculator ---
        Sha256Calculator calculator = new Sha256Calculator();

        // --- Progress ---
        ProgressReporter reporter = new ProgressReporter();
        Map<String, ProgressMessage> progressMap = new HashMap<>();
        reporter.addObserver(message -> progressMap.put(message.getFileName(), message));

        // --- Processor ---
        ChecksumProcessor processor = new ChecksumProcessor(calculator, reporter);
        Map<String, String> results = processor.process(rootDir);

        // --- Check SHA-256 result ---
        String expectedHash = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad";
        assertEquals(expectedHash, results.get("abc.txt"), "SHA-256 for 'abc.txt' is incorrect");

        // --- Check progress ---
        assertEquals(1, progressMap.size(), "Should have one progress notification");
        assertEquals("abc.txt", progressMap.get("abc.txt").getFileName());
    }
}