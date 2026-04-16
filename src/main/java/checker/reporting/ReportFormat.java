/**
 * This enum defines report formats for checksum verification.
 * Supports text and JSON output formats.
 */
package checker.reporting;

public enum ReportFormat {
    TEXT,
    JSON;

    public static ReportFormat fromString(String value) {
        if (value == null) {
            return TEXT;
        }
        switch (value.trim().toLowerCase()) {
            case "text":
            case "plain":
                return TEXT;
            case "json":
                return JSON;
            default:
                throw new IllegalArgumentException("Unknown report format: " + value);
        }
    }
}
