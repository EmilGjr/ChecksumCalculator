/**
 * Този клас тества функционалността на ChecksumProcessor.
 * Включва тестове за изчисляване на контролни суми и обработка на файлове.
 */
package checker;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import checker.core.ChecksumCalculator;
import checker.core.ChecksumProcessor;
import checker.filesystem.DirectoryNode;
import checker.filesystem.FileNode;
import checker.storage.ScanMemento;
import progress.ConsoleObserver;
import progress.ProgressMessage;
import progress.ProgressReporter;

class ChecksumProcessorTest {

    @TempDir
    Path tempDir;

    private ChecksumProcessor processor;
    private ProgressReporter reporter;
    private TestCalculator calculator;

    // Прост калкулатор за тестове (връща низ с дължината на данните)
    static class TestCalculator implements ChecksumCalculator {
        @Override
        public String calculate(java.io.InputStream is) {
            try {
                int size = is.readAllBytes().length;
                return String.valueOf(size);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @BeforeEach
    void setUp() {
        calculator = new TestCalculator();
        reporter = new ProgressReporter();
        processor = new ChecksumProcessor(calculator, reporter);
    }

    @Test
    void testProcessSingleFile() throws IOException {
        // Създаваме реален файл
        java.io.File testFile = tempDir.resolve("file1.txt").toFile();
        try (FileOutputStream fos = new FileOutputStream(testFile)) {
            fos.write("Hello".getBytes()); // 5 байта
        }

        FileNode file = new FileNode(testFile.toPath(), testFile.length());

        // Регистрираме observer за проверка на прогреса
        reporter.addObserver(new ConsoleObserver() {
            @Override
            public void update(ProgressMessage message) {
                assertEquals("file1.txt", message.getFileName());
                assertEquals(1, message.getTotalFiles());
            }
        });

        Map<String, String> result = processor.process(file);

        assertEquals(1, result.size());
        assertEquals("5", result.get("file1.txt"));
    }

    @Test
    void testProcessDirectoryWithFiles() throws IOException {
        // Създаваме реални файлове
        java.io.File fileA = tempDir.resolve("a.txt").toFile();
        try (FileOutputStream fos = new FileOutputStream(fileA)) {
            fos.write("123".getBytes()); // 3 байта
        }

        java.io.File fileB = tempDir.resolve("b.txt").toFile();
        try (FileOutputStream fos = new FileOutputStream(fileB)) {
            fos.write("1234567".getBytes()); // 7 байта
        }

        DirectoryNode dir = new DirectoryNode(tempDir);
        dir.addChild(new FileNode(fileA.toPath(), fileA.length()));
        dir.addChild(new FileNode(fileB.toPath(), fileB.length()));

        Map<String, String> result = processor.process(dir);

        assertEquals(2, result.size());
        assertEquals("3", result.get("a.txt"));
        assertEquals("7", result.get("b.txt"));
    }

    @Test
    void testProcessNestedDirectories() throws IOException {
        // Създаваме реални файлове
        java.io.File file1 = tempDir.resolve("file1.txt").toFile();
        try (FileOutputStream fos = new FileOutputStream(file1)) {
            fos.write("12".getBytes()); // 2 байта
        }

        java.io.File subDir = tempDir.resolve("sub").toFile();
        subDir.mkdir();

        java.io.File file2 = new java.io.File(subDir, "file2.txt");
        try (FileOutputStream fos = new FileOutputStream(file2)) {
            fos.write("1234".getBytes()); // 4 байта
        }

        DirectoryNode root = new DirectoryNode(tempDir);
        DirectoryNode sub = new DirectoryNode(subDir.toPath());
        sub.addChild(new FileNode(file2.toPath(), file2.length()));
        root.addChild(new FileNode(file1.toPath(), file1.length()));
        root.addChild(sub);

        Map<String, String> result = processor.process(root);

        assertEquals(2, result.size());
        assertEquals("2", result.get("file1.txt"));
        assertEquals("4", result.get("sub/file2.txt"));
    }

    @Test
    void testResumeDoesNotDoubleCountCurrentFileBytes() throws IOException {
        java.io.File completedFile = tempDir.resolve("done.txt").toFile();
        try (FileOutputStream fos = new FileOutputStream(completedFile)) {
            fos.write("12345".getBytes());
        }

        java.io.File currentFile = tempDir.resolve("current.txt").toFile();
        try (FileOutputStream fos = new FileOutputStream(currentFile)) {
            fos.write("1234567890".getBytes());
        }

        DirectoryNode root = new DirectoryNode(tempDir);
        root.addChild(new FileNode(completedFile.toPath(), completedFile.length()));
        root.addChild(new FileNode(currentFile.toPath(), currentFile.length()));

        Map<String, String> completedChecksums = new LinkedHashMap<>();
        completedChecksums.put("done.txt", "5");

        ScanMemento resumeState = new ScanMemento(
                tempDir.toString(),
                "SHA256",
                false,
                2,
                1,
                completedFile.length() + currentFile.length(),
                completedFile.length() + 4,
                "current.txt",
                4,
                completedChecksums
        );

        List<Long> observedTotals = new ArrayList<>();
        reporter.addObserver(message -> observedTotals.add(message.getTotalBytesProcessed()));

        Map<String, String> result = processor.process(root, resumeState);

        assertEquals(2, result.size());
        assertEquals("5", result.get("done.txt"));
        assertEquals("10", result.get("current.txt"));
        assertTrue(observedTotals.stream().allMatch(total -> total <= 15));
        assertEquals(15L, observedTotals.get(observedTotals.size() - 1));
    }
}
