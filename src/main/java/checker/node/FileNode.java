package checker.node;

import checker.visitor.FileSystemVisitor;

public class FileNode extends FileSystemNode {
    private final long size;

    public FileNode(String name, long size) {
        super(name);
        this.size = size;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public void accept(FileSystemVisitor visitor) {
        visitor.visit(this);
    }
}