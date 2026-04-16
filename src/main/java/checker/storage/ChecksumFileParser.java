/**
 * This class parses checksum files.
 * It reads a file with hashes and paths, builds a lookup map, and preserves checksum information.
 */
package checker.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChecksumFileParser {

    public static Map<String,String> parse(Path checksumFile) throws IOException {
        Map<String,String> checksums = new LinkedHashMap<>();
        for (String rawLine : Files.readAllLines(checksumFile)) {
            String line = rawLine.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            String[] parts = line.split("\\s+", 2);
            if (parts.length < 2) {
                continue;
            }
            String hash = parts[0].trim();
            String path = parts[1].trim();
            if (path.startsWith("*")) {
                path = path.substring(1);
            }
            path = path.trim();
            if (path.isEmpty()) {
                continue;
            }
            path = path.replace('\\', '/');
            checksums.put(path, hash);
        }
        return checksums;
    }
}
