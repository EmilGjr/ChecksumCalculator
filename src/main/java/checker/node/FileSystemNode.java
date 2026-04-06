package checker.node;
import checker.visitor.FileSystemVisitor;
/**
 * Абстрактен компонент за Composite шаблона.
 */
abstract public class FileSystemNode {
    protected final String name;

    protected FileSystemNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Връща размера в байтове.
     * За файл е фиксиран, за директория се изчислява динамично.
     */
    public abstract long getSize();

    public abstract void accept(FileSystemVisitor visitor);
}