/**
 * This class tests the functionality of ChecksumFileParser.
 * Checks parsing of files with checksums.
 */
package checker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import checker.storage.ChecksumFileParser;

class ChecksumFileParserTest {

    @TempDir
    Path tempDir;

    @Test
    void testParseValidFile() throws IOException {
        Path checksumFile = tempDir.resolve("checksums.txt");
        Files.writeString(checksumFile, "abc123 *file1.txt\nxyz789 *file2.txt\n");

        Map<String, String> checksums = ChecksumFileParser.parse(checksumFile);

        assertEquals(2, checksums.size());
        assertEquals("abc123", checksums.get("file1.txt"));
        assertEquals("xyz789", checksums.get("file2.txt"));
    }

    @Test
    void testParseFileWithAlgorithm() throws IOException {
        Path checksumFile = tempDir.resolve("checksums.txt");
        Files.writeString(checksumFile, "# SHA256\nabc123 *file1.txt\n");

        Map<String, String> checksums = ChecksumFileParser.parse(checksumFile);

        assertEquals(1, checksums.size());
        assertEquals("abc123", checksums.get("file1.txt"));
    }

    @Test
    void testParseEmptyFile() throws IOException {
        Path checksumFile = tempDir.resolve("empty.txt");
        Files.writeString(checksumFile, "");

        Map<String, String> checksums = ChecksumFileParser.parse(checksumFile);

        assertTrue(checksums.isEmpty());
    }

    @Test
    void testParseInvalidFile() {
        assertThrows(IOException.class, () -> {
            ChecksumFileParser.parse(Paths.get("nonexistent.txt"));
        });
    }

    @Test
    void testParseMalformedLine() throws IOException {
        Path checksumFile = tempDir.resolve("malformed.txt");
        Files.writeString(checksumFile, "invalidline\nabc123 *valid.txt\n");

        Map<String, String> checksums = ChecksumFileParser.parse(checksumFile);

        assertEquals(1, checksums.size());
        assertEquals("abc123", checksums.get("valid.txt"));
    }

    @Test
    void testParseJsonChecksumFile() throws IOException {
        Path checksumFile = tempDir.resolve("checksums.json");
        Files.writeString(checksumFile, "[\n" +
                "  {\"path\":\"file1.txt\",\"checksum\":\"abc123\",\"mode\":\"binary\"},\n" +
                "  {\"path\":\"dir/file2.txt\",\"checksum\":\"xyz789\",\"mode\":\"binary\"}\n" +
                "]");

        Map<String, String> checksums = ChecksumFileParser.parse(checksumFile);

        assertEquals(2, checksums.size());
        assertEquals("abc123", checksums.get("file1.txt"));
        assertEquals("xyz789", checksums.get("dir/file2.txt"));
    }

}