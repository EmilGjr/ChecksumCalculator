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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChecksumFileParser {
    private static final Pattern JSON_OBJECT_PATTERN = Pattern.compile("\\{(.*?)\\}", Pattern.DOTALL);
    private static final Pattern JSON_PATH_PATTERN = Pattern.compile("\"path\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"");
    private static final Pattern JSON_CHECKSUM_PATTERN = Pattern.compile("\"checksum\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"");

    public static Map<String,String> parse(Path checksumFile) throws IOException {
        String content = Files.readString(checksumFile);
        String trimmed = content.trim();
        if (trimmed.startsWith("[")) {
            return parseJson(trimmed);
        }

        Map<String,String> checksums = new LinkedHashMap<>();
        for (String rawLine : content.lines().toList()) {
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

    private static Map<String, String> parseJson(String json) {
        Map<String, String> checksums = new LinkedHashMap<>();
        Matcher objectMatcher = JSON_OBJECT_PATTERN.matcher(json);
        while (objectMatcher.find()) {
            String objectBody = objectMatcher.group(1);
            String path = extractJsonField(objectBody, JSON_PATH_PATTERN);
            String checksum = extractJsonField(objectBody, JSON_CHECKSUM_PATTERN);
            if (path == null || checksum == null || path.isBlank()) {
                continue;
            }
            checksums.put(path.replace('\\', '/'), checksum);
        }
        return checksums;
    }

    private static String extractJsonField(String jsonObject, Pattern pattern) {
        Matcher matcher = pattern.matcher(jsonObject);
        if (!matcher.find()) {
            return null;
        }
        return unescapeJson(matcher.group(1));
    }

    private static String unescapeJson(String value) {
        return value
                .replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\/", "/")
                .replace("\\b", "\b")
                .replace("\\f", "\f")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");
    }
}

