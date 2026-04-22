/**
 * This class observes progress and prints information to the console.
 * It shows the current file, percent complete, and ETA during processing.
 */
package progress;

import java.time.Duration;
import java.time.Instant;

// Observer that displays progress in the console
public class ConsoleObserver implements Observer {

    private String currentFile = "(none)";
    private Instant startTime;
    private long lastTotalBytes = 0;

    @Override
    public void update(ProgressMessage message) {
        if (startTime == null) {
            startTime = Instant.now();
        }

        if (!message.getFileName().equals(currentFile)) {
            currentFile = message.getFileName();
            System.out.println();
        }

        long bytesProcessed = message.getTotalBytesProcessed();
        long totalBytes = message.getTotalBytes();
        long displayedBytesProcessed = totalBytes > 0
                ? Math.min(bytesProcessed, totalBytes)
                : bytesProcessed;
        long currentFileBytes = message.getCurrentFileBytes();
        long currentFileSize = message.getCurrentFileSize();
        long displayedCurrentFileBytes = currentFileSize > 0
                ? Math.min(currentFileBytes, currentFileSize)
                : currentFileBytes;
        double percent = totalBytes > 0 ? (displayedBytesProcessed * 100.0) / totalBytes : 0.0;
        Instant now = Instant.now();
        long elapsedSeconds = Math.max(1, Duration.between(startTime, now).getSeconds());
        long bytesPerSecond = displayedBytesProcessed / elapsedSeconds;
        String eta = "?";
        if (bytesPerSecond > 0 && totalBytes > displayedBytesProcessed) {
            long remaining = totalBytes - displayedBytesProcessed;
            long etaSeconds = remaining / bytesPerSecond;
            eta = formatDuration(etaSeconds);
        }

        String messageText = String.format(
                "\rProcessing %s [%d/%d bytes] overall [%d/%d bytes] %.1f%% ETA %s",
                message.getFileName(),
                displayedCurrentFileBytes,
                currentFileSize,
                displayedBytesProcessed,
                totalBytes,
                percent,
                eta
        );

        System.out.print(messageText);
        System.out.flush();
    }

    private String formatDuration(long seconds) {
        long minutes = seconds / 60;
        long remainder = seconds % 60;
        return String.format("%dm %ds", minutes, remainder);
    }
}
