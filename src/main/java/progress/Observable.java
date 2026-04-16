/**
 * This interface defines the observable object in the Observer pattern.
 * It allows adding and removing observers and notifying them of changes.
 */
package progress;

public interface Observable {

    void addObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers(ProgressMessage message);

}