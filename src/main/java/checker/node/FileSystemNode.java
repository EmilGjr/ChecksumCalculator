package checker.node;

abstract public class FileSystemNode {
    protected final String name;

    protected FileSystemNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract long getSize();
}