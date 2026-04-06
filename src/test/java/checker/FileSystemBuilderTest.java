package checker;

import checker.node.FileSystemBuilder;
import checker.node.FileSystemNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileSystemBuilderTest {

    @TempDir
    Path tempDir;

    @Test
    void testBuildHierarchyFromRealFileSystem() throws IOException {
        // 1. Подготовка на тестова структура на диска
        // tempDir/
        //  ├── file1.dat (10 bytes)
        //  └── subdir/
        //      └── file2.dat (20 bytes)

        Path file1 = tempDir.resolve("file1.dat");
        Files.write(file1, new byte[10]);

        Path subdir = tempDir.resolve("subdir");
        Files.createDirectory(subdir);

        Path file2 = subdir.resolve("file2.dat");
        Files.write(file2, new byte[20]);

        // 2. Изпълнение на Builder-а
        FileSystemBuilder builder = new FileSystemBuilder(false);
        FileSystemNode root = builder.build(tempDir);

        // 3. Проверки
        assertNotNull(root, "Коренният възел не трябва да е null");
        assertEquals(30, root.getSize(), "Общият размер на изграденото дърво трябва да е 30 байта.");
        assertEquals(tempDir.getFileName().toString(), root.getName());
    }
}