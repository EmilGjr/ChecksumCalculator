/**
 * This class tests the functionality of ConsoleObserver.
 * Checks the output of progress to the console.
 */
package progress;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ConsoleObserverTest {

    @Test
    void testUpdate() {
        // Redirect System.out to capture output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            ConsoleObserver observer = new ConsoleObserver();
            ProgressMessage message = new ProgressMessage("test.txt", 1, 1, 50, 100, 50, 100);

            observer.update(message);

            String output = outputStream.toString();
            assertTrue(output.contains("test.txt"));
            assertTrue(output.contains("50"));
            assertTrue(output.contains("100"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void testUpdateWithETA() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            ConsoleObserver observer = new ConsoleObserver();
            ProgressMessage message = new ProgressMessage("file.txt", 1, 2, 0, 0, 0, 100);

            observer.update(message);

            String output = outputStream.toString();
            assertTrue(output.contains("file.txt"));
            assertTrue(output.contains("ETA"));
        } finally {
            System.setOut(originalOut);
        }
    }
}