package checker.node;

import checker.visitor.FileSystemVisitor;
import java.util.ArrayList;
import java.util.List;

public class DirectoryNode extends FileSystemNode {
    private final List<FileSystemNode> children = new ArrayList<>();

    public DirectoryNode(String name) {
        super(name);
    }

    public void addChild(FileSystemNode node) {
        children.add(node);
    }

    /**
     * Връща копие на списъка, за да предотврати нежелани промени отвън.
     */
    public List<FileSystemNode> getChildren() {
        return new ArrayList<>(children);
    }

    @Override
    public long getSize() {
        return children.stream()
                .mapToLong(FileSystemNode::getSize)
                .sum();
    }

    @Override
    public void accept(FileSystemVisitor visitor) {
        visitor.visit(this);
    }
}