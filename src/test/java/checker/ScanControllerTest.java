/**
 * Този клас тества функционалността на ScanController.
 * Проверява управление паузой, возобновлением и остановкой сканирования.
 */
package checker;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import checker.control.ScanController;

class ScanControllerTest {

    @Test
    void testInitialState() {
        ScanController controller = new ScanController();
        assertFalse(controller.isPaused());
        assertFalse(controller.isStopRequested());
    }

    @Test
    void testPause() {
        ScanController controller = new ScanController();
        controller.pause();
        assertTrue(controller.isPaused());
        assertFalse(controller.isStopRequested());
    }

    @Test
    void testResume() {
        ScanController controller = new ScanController();
        controller.pause();
        controller.resume();
        assertFalse(controller.isPaused());
        assertFalse(controller.isStopRequested());
    }

    @Test
    void testRequestStop() {
        ScanController controller = new ScanController();
        controller.requestStop();
        assertTrue(controller.isStopRequested());
    }

    @Test
    void testWaitIfPaused() throws InterruptedException {
        ScanController controller = new ScanController();
        controller.pause();

        Thread thread = new Thread(() -> {
            controller.waitIfPaused();
        });
        thread.start();
        Thread.sleep(100); // Give thread time to start and wait
        assertTrue(thread.isAlive()); // Thread should be waiting

        controller.resume();
        Thread.sleep(100); // Give thread time to resume
        thread.join(1000);
        assertFalse(thread.isAlive());
    }
}