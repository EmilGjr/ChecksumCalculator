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

            // JSON format
            if (line.startsWith("{") && line.contains("\"path\"")) {

                int p1 = line.indexOf("\"path\":\"") + 8;
                int p2 = line.indexOf("\"", p1);

                int h1 = line.indexOf("\"checksum\":\"") + 13;
                int h2 = line.indexOf("\"", h1);

                if (p1 > 7 && p2 > p1 && h1 > 12 && h2 > h1) {
                    String path = line.substring(p1, p2);
                    String hash = line.substring(h1, h2);

                    path = normalize(path);

                    checksums.put(path, hash);
                }

                continue;
            }

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

            path = normalize(path);

            checksums.put(path, hash);
        }

        return checksums;
    }

    private static String normalize(String p) {
        return p.replace('\\', '/')
                .toLowerCase()
                .replaceAll("^\\./", "")
                .replaceAll("/+", "/");
    }
}
