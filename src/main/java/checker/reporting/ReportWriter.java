/**
 * This class is responsible for writing checksum reports and verification results.
 * It supports writing to files and printing to the console in different formats.
 */
package checker.reporting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportWriter {

    public static void printChecksumReport(Map<String,String> results, ReportFormat format) {
        switch (format) {
            case JSON:
                printChecksumJson(results);
                break;
            case TEXT:
            default:
                printChecksumText(results);
                break;
        }
    }

    public static void printVerificationReport(List<VerificationStatus> statuses, ReportFormat format) {
        switch (format) {
            case JSON:
                printVerificationJson(statuses);
                break;
            case TEXT:
            default:
                printVerificationText(statuses);
                break;
        }
    }

    public static void saveChecksumReport(Map<String, String> results, Path file) throws IOException {
        saveChecksumReport(results, file, ReportFormat.TEXT);
    }

    public static void saveChecksumReport(Map<String, String> results, Path file, ReportFormat format) throws IOException {
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }

        if (format == ReportFormat.JSON) {
            Files.writeString(file, buildChecksumJson(results));
        } else {
            String content = results.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> entry.getValue() + " *" + normalizePath(entry.getKey()))
                    .collect(Collectors.joining("\n"));
            Files.writeString(file, content);
        }
    }

    public static void saveVerificationReport(List<VerificationStatus> statuses, Path file) throws IOException {
        saveVerificationReport(statuses, file, ReportFormat.TEXT);
    }

    public static void saveVerificationReport(List<VerificationStatus> statuses, Path file, ReportFormat format) throws IOException {
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }

        if (format == ReportFormat.JSON) {
            Files.writeString(file, buildVerificationJson(statuses));
        } else {
            String content = statuses.stream()
                    .sorted((a, b) -> a.getPath().compareToIgnoreCase(b.getPath()))
                    .map(status -> normalizePath(status.getPath()) + ": " + status.getResult())
                    .collect(Collectors.joining("\n"));
            Files.writeString(file, content);
        }
    }

    private static String buildChecksumJson(Map<String,String> results) {
        return results.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> "  {\"path\":\"" + escapeJson(normalizePath(entry.getKey())) + "\",\"checksum\":\"" + escapeJson(entry.getValue()) + "\",\"mode\":\"binary\"}")
                .collect(Collectors.joining(",\n", "[\n", "\n]"));
    }

    private static String buildVerificationJson(List<VerificationStatus> statuses) {
        return statuses.stream()
                .sorted((a, b) -> a.getPath().compareToIgnoreCase(b.getPath()))
                .map(status -> "  {\"path\":\"" + escapeJson(normalizePath(status.getPath())) + "\",\"status\":\"" + status.getResult() + "\",\"expected\":\"" + escapeJson(status.getExpectedChecksum()) + "\",\"actual\":\"" + escapeJson(status.getActualChecksum()) + "\"}")
                .collect(Collectors.joining(",\n", "[\n", "\n]"));
    }

    private static void printChecksumText(Map<String,String> results) {
        results.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> System.out.println(entry.getValue() + " *" + normalizePath(entry.getKey())));
    }

    private static void printChecksumJson(Map<String,String> results) {
        String body = results.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> "  {\"path\":\"" + escapeJson(normalizePath(entry.getKey())) + "\",\"checksum\":\"" + escapeJson(entry.getValue()) + "\",\"mode\":\"binary\"}")
                .collect(Collectors.joining(",\n"));
        System.out.println("[");
        System.out.println(body);
        System.out.println("]");
    }

    private static void printVerificationText(List<VerificationStatus> statuses) {
        statuses.stream()
                .sorted((a, b) -> a.getPath().compareToIgnoreCase(b.getPath()))
                .forEach(status -> System.out.println(normalizePath(status.getPath()) + ": " + status.getResult()));
    }

    private static void printVerificationJson(List<VerificationStatus> statuses) {
        String body = statuses.stream()
                .sorted((a, b) -> a.getPath().compareToIgnoreCase(b.getPath()))
                .map(status -> "  {\"path\":\"" + escapeJson(normalizePath(status.getPath())) + "\",\"status\":\"" + status.getResult() + "\",\"expected\":\"" + escapeJson(status.getExpectedChecksum()) + "\",\"actual\":\"" + escapeJson(status.getActualChecksum()) + "\"}")
                .collect(Collectors.joining(",\n"));
        System.out.println("[");
        System.out.println(body);
        System.out.println("]");
    }

    private static String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String normalizePath(String path) {
        return path.replace('\\', '/');
    }
}
