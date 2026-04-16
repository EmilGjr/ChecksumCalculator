/**
 * This class tests the functionality of VerificationStatus.
 * Checks creation and properties of verification status objects.
 */
package checker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import checker.reporting.VerificationResult;
import checker.reporting.VerificationStatus;

class VerificationStatusTest {

    @Test
    void testConstructorAndGetters() {
        VerificationStatus status = new VerificationStatus("file1.txt", VerificationResult.OK, "abc123", "abc123");

        assertEquals("file1.txt", status.getPath());
        assertEquals(VerificationResult.OK, status.getResult());
        assertEquals("abc123", status.getExpectedChecksum());
        assertEquals("abc123", status.getActualChecksum());
    }

    @Test
    void testToString() {
        VerificationStatus status = new VerificationStatus("file1.txt", VerificationResult.OK, "abc123", "abc123");
        String str = status.toString();
        assertTrue(str.contains("file1.txt"));
        assertTrue(str.contains("OK"));
    }

    @Test
    void testModifiedResult() {
        VerificationStatus status = new VerificationStatus("file1.txt", VerificationResult.MODIFIED, "old", "new");
        assertEquals(VerificationResult.MODIFIED, status.getResult());
        assertEquals("old", status.getExpectedChecksum());
        assertEquals("new", status.getActualChecksum());
    }

    @Test
    void testNewResult() {
        VerificationStatus status = new VerificationStatus("file1.txt", VerificationResult.NEW, null, "new");
        assertEquals(VerificationResult.NEW, status.getResult());
        assertNull(status.getExpectedChecksum());
        assertEquals("new", status.getActualChecksum());
    }

    @Test
    void testRemovedResult() {
        VerificationStatus status = new VerificationStatus("file1.txt", VerificationResult.REMOVED, "old", null);
        assertEquals(VerificationResult.REMOVED, status.getResult());
        assertEquals("old", status.getExpectedChecksum());
        assertNull(status.getActualChecksum());
    }
}