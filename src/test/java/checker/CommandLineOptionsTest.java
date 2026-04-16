/**
 * This class tests the functionality of CommandLineOptions.
 * Checks parsing of command line arguments.
 */
package checker;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import checker.cli.CommandLineOptions;
import checker.reporting.ReportFormat;

class CommandLineOptionsTest {

    @Test
    void testParseCalculateMode() {
        CommandLineOptions options = CommandLineOptions.parse(new String[]{"-m=calculate", "-p=src"});
        assertEquals("calculate", options.getMode());
        assertEquals(Paths.get("src"), options.getPath());
    }

    @Test
    void testParseVerifyMode() {
        CommandLineOptions options = CommandLineOptions.parse(new String[]{"-m=verify", "-p=src", "-c=checksums.txt"});
        assertEquals("verify", options.getMode());
        assertEquals(Paths.get("src"), options.getPath());
        assertEquals(Paths.get("checksums.txt"), options.getChecksumsFile());
    }

    @Test
    void testParseWithAlgorithm() {
        CommandLineOptions options = CommandLineOptions.parse(new String[]{"-m=calculate", "-p=src", "-a=sha256"});
        assertEquals("sha256", options.getAlgorithm());
    }

    @Test
    void testParseWithVerificationOutput() {
        CommandLineOptions options = CommandLineOptions.parse(new String[]{"-m=verify", "-p=src", "-c=checksums.txt", "-t=report.txt"});
        assertEquals(Paths.get("report.txt"), options.getVerificationOutput());
    }

    @Test
    void testParseNoFollowLinks() {
        CommandLineOptions options = CommandLineOptions.parse(new String[]{"-m=calculate", "-p=src", "-nfl"});
        assertFalse(options.isFollowLinks());
    }

    @Test
    void testParseDefaultValues() {
        CommandLineOptions options = CommandLineOptions.parse(new String[]{"-m=calculate"});
        assertEquals(Paths.get("."), options.getPath());
        assertEquals(Paths.get("checksums.txt"), options.getChecksumsFile());
        assertEquals("sha256", options.getAlgorithm());
        assertEquals("scan.state", options.getStateFile().toString());
        assertTrue(options.isFollowLinks());
    }

    @Test
    void testParseReportFormat() {
        CommandLineOptions options = CommandLineOptions.parse(new String[]{"-m=calculate", "-f=json"});
        assertEquals(ReportFormat.JSON, options.getReportFormat());
    }

    @Test
    void testParseStateFile() {
        CommandLineOptions options = CommandLineOptions.parse(new String[]{"-m=calculate", "-s=state.properties"});
        assertEquals(Paths.get("state.properties"), options.getStateFile());
    }

    @Test
    void testParseInvalidMode() {
        IllegalArgumentException ignored = assertThrows(IllegalArgumentException.class, () -> {
            CommandLineOptions.parse(new String[]{"-m=invalid"});
        });
        assertEquals("Unknown mode: invalid", ignored.getMessage());
    }

    @Test
    void testParseHelp() {
        CommandLineOptions options = CommandLineOptions.parse(new String[]{"--help"});
        assertTrue(options.isHelp());
    }

    @Test
    void testParsePauseMode() {
        CommandLineOptions options = CommandLineOptions.parse(new String[]{"-m=pause"});
        assertTrue(options.isPauseMode());
    }

    @Test
    void testParseResumeMode() {
        CommandLineOptions options = CommandLineOptions.parse(new String[]{"-m=resume"});
        assertTrue(options.isResumeMode());
    }
}