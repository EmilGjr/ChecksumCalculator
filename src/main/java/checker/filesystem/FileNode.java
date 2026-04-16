/**
 * This class represents a file in the file system tree.
 * It extends FileSystemNode and stores the file size.
 */
package checker.filesystem;

import java.nio.file.Path;

public class FileNode extends FileSystemNode {
    private final long size;

    public FileNode(Path path, long size) {
        super(path, path.getFileName().toString());
        this.size = size;
    }

    public FileNode(String name, long size) {
        super(name);
        this.size = size;
    }

    @Override
    public long getSize() {
        return size;
    }
}
