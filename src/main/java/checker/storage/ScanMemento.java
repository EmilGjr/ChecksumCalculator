/**
 * This class represents a snapshot of scan state.
 * It is used to save and restore progress during pause/resume.
 */
package checker.storage;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ScanMemento {
    private final String rootPath;
    private final String algorithm;
    private final boolean followLinks;
    private final int totalFiles;
    private final int processedFiles;
    private final long totalBytes;
    private final long totalBytesProcessed;
    private final String currentFile;
    private final long currentFileBytes;
    private final Map<String, String> completedChecksums;

    public ScanMemento(String rootPath,
                       String algorithm,
                       boolean followLinks,
                       int totalFiles,
                       int processedFiles,
                       long totalBytes,
                       long totalBytesProcessed,
                       String currentFile,
                       long currentFileBytes,
                       Map<String, String> completedChecksums) {
        this.rootPath = rootPath;
        this.algorithm = algorithm;
        this.followLinks = followLinks;
        this.totalFiles = totalFiles;
        this.processedFiles = processedFiles;
        this.totalBytes = totalBytes;
        this.totalBytesProcessed = totalBytesProcessed;
        this.currentFile = currentFile;
        this.currentFileBytes = currentFileBytes;
        this.completedChecksums = new LinkedHashMap<>(completedChecksums);
    }

    public String getRootPath() {
        return rootPath;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public boolean isFollowLinks() {
        return followLinks;
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public int getProcessedFiles() {
        return processedFiles;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public long getTotalBytesProcessed() {
        return totalBytesProcessed;
    }

    public String getCurrentFile() {
        return currentFile;
    }

    public long getCurrentFileBytes() {
        return currentFileBytes;
    }

    public Map<String, String> getCompletedChecksums() {
        return Collections.unmodifiableMap(completedChecksums);
    }
}
