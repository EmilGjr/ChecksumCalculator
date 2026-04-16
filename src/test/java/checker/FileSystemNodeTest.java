/**
 * This class tests the functionality of FileSystemNode and its descendants.
 * Checks the properties and behavior of nodes in the tree.
 */
package checker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import checker.filesystem.DirectoryNode;
import checker.filesystem.FileNode;

class FileSystemNodeTest {

    @Test
    @DisplayName("Тест на FileNode с Path")
    void testFileNodeWithPath() {
        java.nio.file.Path path = java.nio.file.Path.of("test/file.txt");
        FileNode fileNode = new FileNode(path, 123);

        assertEquals("file.txt", fileNode.getName());
        assertEquals(path, fileNode.getPath());
        assertEquals(123, fileNode.getSize());
    }

    @Test
    @DisplayName("Тест на DirectoryNode с Path")
    void testDirectoryNodeWithPath() {
        java.nio.file.Path path = java.nio.file.Path.of("test/dir");
        DirectoryNode dirNode = new DirectoryNode(path);

        assertEquals("dir", dirNode.getName());
        assertEquals(path, dirNode.getPath());
    }

    @Test
    @DisplayName("Изчисляване на общия размер на вложени папки и файлове")
    void testCompositeSize() {
        // Създаваме структура:
        // root/ (общо: 300)
        // ├── config.json (100)
        // └── data/ (200)
        //     ├── img1.png (150)
        //     └── log.txt (50)

        DirectoryNode root = new DirectoryNode("root");
        root.addChild(new FileNode("config.json", 100));

        DirectoryNode dataDir = new DirectoryNode("data");
        dataDir.addChild(new FileNode("img1.png", 150));
        dataDir.addChild(new FileNode("log.txt", 50));

        root.addChild(dataDir);

        assertEquals(300, root.getSize(), "Общият размер на root трябва да е 300.");
        assertEquals(200, dataDir.getSize(), "Размерът на папка 'data' трябва да е 200.");
    }
}