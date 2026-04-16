/**
 * Този клас тества функционалността на ReportWriter.
 * Проверява записването на отчети за контрольные суммы и верификацию.
 */
package checker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import checker.reporting.ReportFormat;
import checker.reporting.ReportWriter;
import checker.reporting.VerificationResult;
import checker.reporting.VerificationStatus;

class ReportWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void testSaveChecksumReport() throws IOException {
        Path outputFile = tempDir.resolve("checksums.txt");
        Map<String, String> checksums = new LinkedHashMap<>();
        checksums.put("file1.txt", "abc123");
        checksums.put("file2.txt", "xyz789");

        ReportWriter.saveChecksumReport(checksums, outputFile);

        List<String> lines = Files.readAllLines(outputFile);
        assertEquals(2, lines.size());
        assertTrue(lines.contains("abc123 *file1.txt"));
        assertTrue(lines.contains("xyz789 *file2.txt"));
    }

    @Test
    void testSaveVerificationReport() throws IOException {
        Path outputFile = tempDir.resolve("report.txt");
        List<VerificationStatus> statuses = List.of(
            new VerificationStatus("file1.txt", VerificationResult.OK, "abc123", "abc123"),
            new VerificationStatus("file2.txt", VerificationResult.MODIFIED, "xyz789", "new456")
        );

        ReportWriter.saveVerificationReport(statuses, outputFile);

        List<String> lines = Files.readAllLines(outputFile);
        assertTrue(lines.size() > 0);
        assertTrue(lines.stream().anyMatch(line -> line.contains("file1.txt: OK")));
        assertTrue(lines.stream().anyMatch(line -> line.contains("file2.txt: MODIFIED")));
    }

    @Test
    void testPrintVerificationReport() {
        List<VerificationStatus> statuses = List.of(
            new VerificationStatus("file1.txt", VerificationResult.OK, "abc123", "abc123")
        );

        // This will print to console, but we can't easily test console output
        // In a real test, you might redirect System.out
        assertDoesNotThrow(() -> ReportWriter.printVerificationReport(statuses, ReportFormat.TEXT));
    }
}