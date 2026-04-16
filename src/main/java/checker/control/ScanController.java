/**
 * This class manages scanning state, including pause, resume, and stop.
 * It provides control methods for the scanning process.
 */
package checker.control;

public class ScanController {
    private volatile boolean paused;
    private volatile boolean stopRequested;
    private final Object lock = new Object();

    public void pause() {
        synchronized (lock) {
            paused = true;
        }
    }

    public void resume() {
        synchronized (lock) {
            paused = false;
            lock.notifyAll();
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isStopRequested() {
        return stopRequested;
    }

    public void requestStop() {
        stopRequested = true;
        resume();
    }

    public void waitIfPaused() {
        synchronized (lock) {
            while (paused) {
                try {
                    lock.wait();
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}
