/**
 * This class represents a directory in the file system tree.
 * It extends FileSystemNode and contains a list of child nodes.
 */
package checker.filesystem;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DirectoryNode extends FileSystemNode {
    private final List<FileSystemNode> children = new ArrayList<>();

    public DirectoryNode(Path path) {
        super(path, path.getFileName().toString());
    }

    public DirectoryNode(String name) {
        super(name);
    }

    public void addChild(FileSystemNode node) {
        children.add(node);
    }

    public List<FileSystemNode> getChildren() {
        return new ArrayList<>(children);
    }

    @Override
    public long getSize() {
        return children.stream()
                .mapToLong(FileSystemNode::getSize)
                .sum();
    }
}
