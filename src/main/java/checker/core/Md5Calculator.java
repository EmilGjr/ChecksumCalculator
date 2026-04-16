/**
 * This class implements MD5 checksum calculation.
 * It uses Java's MessageDigest to hash files.
 */
package checker.core;

import java.io.InputStream;
import java.security.MessageDigest;

public class Md5Calculator implements ChecksumCalculator {
    @Override
    public String calculate(InputStream is) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[4096];

            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
            byte[] hashBytes = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error calculating MD5", e);
        }
    }
}
