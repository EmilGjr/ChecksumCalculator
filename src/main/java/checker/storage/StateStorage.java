/**
 * This class manages saving and loading scan state.
 * It uses serialization to persist progress.
 */
package checker.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class StateStorage {

    private static final String PREFIX_FILE = "file.";

    public static void save(ScanMemento memento, Path stateFile) throws IOException {
        Properties properties = new Properties();
        properties.setProperty("rootPath", memento.getRootPath());
        properties.setProperty("algorithm", memento.getAlgorithm());
        properties.setProperty("followLinks", String.valueOf(memento.isFollowLinks()));
        properties.setProperty("totalFiles", String.valueOf(memento.getTotalFiles()));
        properties.setProperty("processedFiles", String.valueOf(memento.getProcessedFiles()));
        properties.setProperty("totalBytes", String.valueOf(memento.getTotalBytes()));
        properties.setProperty("totalBytesProcessed", String.valueOf(memento.getTotalBytesProcessed()));
        properties.setProperty("currentFile", memento.getCurrentFile() == null ? "" : memento.getCurrentFile());
        properties.setProperty("currentFileBytes", String.valueOf(memento.getCurrentFileBytes()));

        for (Map.Entry<String, String> entry : memento.getCompletedChecksums().entrySet()) {
            properties.setProperty(PREFIX_FILE + entry.getKey(), entry.getValue());
        }

        Path parent = stateFile.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (OutputStream os = Files.newOutputStream(stateFile)) {
            properties.store(os, "Scan state file");
        }
    }

    public static ScanMemento load(Path stateFile) throws IOException {
        Properties properties = new Properties();
        try (InputStream is = Files.newInputStream(stateFile)) {
            properties.load(is);
        }

        Map<String, String> checksums = new LinkedHashMap<>();
        for (String propertyName : properties.stringPropertyNames()) {
            if (propertyName.startsWith(PREFIX_FILE)) {
                String relativePath = propertyName.substring(PREFIX_FILE.length());
                checksums.put(relativePath, properties.getProperty(propertyName));
            }
        }

        String rootPath = properties.getProperty("rootPath", "");
        String algorithm = properties.getProperty("algorithm", "sha256");
        boolean followLinks = Boolean.parseBoolean(properties.getProperty("followLinks", "false"));
        int totalFiles = Integer.parseInt(properties.getProperty("totalFiles", "0"));
        int processedFiles = Integer.parseInt(properties.getProperty("processedFiles", "0"));
        long totalBytes = Long.parseLong(properties.getProperty("totalBytes", "0"));
        long totalBytesProcessed = Long.parseLong(properties.getProperty("totalBytesProcessed", "0"));
        String currentFile = properties.getProperty("currentFile", "");
        long currentFileBytes = Long.parseLong(properties.getProperty("currentFileBytes", "0"));

        return new ScanMemento(
                rootPath,
                algorithm,
                followLinks,
                totalFiles,
                processedFiles,
                totalBytes,
                totalBytesProcessed,
                currentFile.isEmpty() ? null : currentFile,
                currentFileBytes,
                checksums);
    }
}
