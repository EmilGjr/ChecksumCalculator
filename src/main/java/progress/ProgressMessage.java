/**
 * This class contains information about current processing progress.
 * It includes the current file, processed bytes, and total size.
 */
package progress;

public class ProgressMessage {

    private final String fileName;
    private final int processedFiles;
    private final int totalFiles;
    private final long currentFileBytes;
    private final long currentFileSize;
    private final long totalBytesProcessed;
    private final long totalBytes;

    public ProgressMessage(String fileName,
                           int processedFiles,
                           int totalFiles) {
        this(fileName, processedFiles, totalFiles, 0, 0, 0, 0);
    }

    public ProgressMessage(String fileName,
                           int processedFiles,
                           int totalFiles,
                           long currentFileBytes,
                           long currentFileSize,
                           long totalBytesProcessed,
                           long totalBytes) {
        this.fileName = fileName;
        this.processedFiles = processedFiles;
        this.totalFiles = totalFiles;
        this.currentFileBytes = currentFileBytes;
        this.currentFileSize = currentFileSize;
        this.totalBytesProcessed = totalBytesProcessed;
        this.totalBytes = totalBytes;
    }

    public String getFileName() {
        return fileName;
    }

    public int getProcessedFiles() {
        return processedFiles;
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public long getCurrentFileBytes() {
        return currentFileBytes;
    }

    public long getCurrentFileSize() {
        return currentFileSize;
    }

    public long getTotalBytesProcessed() {
        return totalBytesProcessed;
    }

    public long getTotalBytes() {
        return totalBytes;
    }
}