/**
 * This abstract class is the base for nodes in the file system tree.
 * It defines common properties like path and name.
 */
package checker.filesystem;

import java.nio.file.Path;

abstract public class FileSystemNode {
    protected final String name;
    protected final Path path;

    protected FileSystemNode(Path path, String name) {
        this.path = path;
        this.name = name;
    }

    protected FileSystemNode(String name) {
        this(Path.of(name), name);
    }

    public String getName() {
        return name;
    }

    public Path getPath() {
        return path;
    }

    public String getRelativePath(Path root) {
        if (root == null) {
            return path.toString();
        }
        try {
            return root.relativize(path).toString();
        } catch (IllegalArgumentException e) {
            return path.toString();
        }
    }

    public abstract long getSize();
}
