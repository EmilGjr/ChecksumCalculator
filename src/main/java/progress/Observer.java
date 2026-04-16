/**
 * This interface defines an observer in the Observer pattern.
 * It receives progress updates from the observable object.
 */
package progress;

public interface Observer {

    void update(ProgressMessage message);

}