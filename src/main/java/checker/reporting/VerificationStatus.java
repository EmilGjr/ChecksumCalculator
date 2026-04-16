/**
 * This class represents the verification status for a specific file.
 * It contains the file path and the result of checksum comparison.
 */
package checker.reporting;

public class VerificationStatus {
    private final String path;
    private final VerificationResult result;
    private final String expectedChecksum;
    private final String actualChecksum;

    public VerificationStatus(String path,
                              VerificationResult result,
                              String expectedChecksum,
                              String actualChecksum) {
        this.path = path;
        this.result = result;
        this.expectedChecksum = expectedChecksum;
        this.actualChecksum = actualChecksum;
    }

    public String getPath() {
        return path;
    }

    public VerificationResult getResult() {
        return result;
    }

    public String getExpectedChecksum() {
        return expectedChecksum;
    }

    public String getActualChecksum() {
        return actualChecksum;
    }

    @Override
    public String toString() {
        return path + ": " + result;
    }
}
