/**
 * This class manages progress reporting using the Observer pattern.
 * It notifies all registered observers of updates.
 */
package progress;

import java.util.ArrayList;
import java.util.List;

// Class that sends progress information to observers
public class ProgressReporter implements Observable {

    private List<Observer> observers =
            new ArrayList<>();

    @Override
    public void addObserver(Observer observer) {

        observers.add(observer);

    }

    @Override
    public void removeObserver(Observer observer) {

        observers.remove(observer);

    }

    @Override
    public void notifyObservers(
            ProgressMessage message) {

        for(Observer o : observers) {

            o.update(message);

        }

    }

}