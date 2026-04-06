package checker;

import checker.node.DirectoryNode;
import checker.node.FileNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileSystemNodeTest {

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