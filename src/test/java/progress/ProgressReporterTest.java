/**
 * This class tests the functionality of ProgressReporter.
 * Checks adding observers and notifications.
 */
package progress;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class ProgressReporterTest {

    @Test
    void testAddObserver() {
        ProgressReporter reporter = new ProgressReporter();
        TestObserver observer = new TestObserver();
        reporter.addObserver(observer);

        ProgressMessage message = new ProgressMessage("file.txt", 1, 1, 100, 100, 100, 100);
        reporter.notifyObservers(message);

        assertEquals(1, observer.messages.size());
        assertEquals(message, observer.messages.get(0));
    }

    @Test
    void testMultipleObservers() {
        ProgressReporter reporter = new ProgressReporter();
        TestObserver observer1 = new TestObserver();
        TestObserver observer2 = new TestObserver();
        reporter.addObserver(observer1);
        reporter.addObserver(observer2);

        ProgressMessage message = new ProgressMessage("file.txt", 1, 1);
        reporter.notifyObservers(message);

        assertEquals(1, observer1.messages.size());
        assertEquals(1, observer2.messages.size());
    }

    @Test
    void testNotifyObservers() {
        ProgressReporter reporter = new ProgressReporter();
        TestObserver observer = new TestObserver();
        reporter.addObserver(observer);

        ProgressMessage message1 = new ProgressMessage("file1.txt", 1, 2);
        ProgressMessage message2 = new ProgressMessage("file2.txt", 2, 2);

        reporter.notifyObservers(message1);
        reporter.notifyObservers(message2);

        assertEquals(2, observer.messages.size());
        assertEquals(message1, observer.messages.get(0));
        assertEquals(message2, observer.messages.get(1));
    }

    private static class TestObserver implements Observer {
        List<ProgressMessage> messages = new ArrayList<>();

        @Override
        public void update(ProgressMessage message) {
            messages.add(message);
        }
    }
}