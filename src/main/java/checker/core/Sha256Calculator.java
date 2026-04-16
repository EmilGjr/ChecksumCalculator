/**
 * This class implements SHA-256 checksum calculation.
 * It uses Java's MessageDigest for secure file hashing.
 */
package checker.core;

import java.io.InputStream;
import java.security.MessageDigest;

// SHA-256 checksum calculator implementation
// Works similarly to Md5Calculator but uses SHA-256
public class Sha256Calculator implements ChecksumCalculator {

    @Override
    public String calculate(InputStream is) {

        try {

            // Create SHA-256 algorithm
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] buffer = new byte[4096];

            int bytesRead;

            // Read the file in chunks
            while ((bytesRead = is.read(buffer)) != -1) {

                digest.update(buffer, 0, bytesRead);

            }

            // Get the hash result
            byte[] hashBytes = digest.digest();

            // Convert it to a hex string
            StringBuilder sb =
                    new StringBuilder();

            for (byte b : hashBytes) {

                sb.append(
                        String.format("%02x", b)
                );

            }

            return sb.toString();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error calculating SHA-256",
                    e
            );

        }

    }

}
