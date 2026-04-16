/**
 * This interface defines the checksum calculation method.
 * It is implemented by concrete classes such as Md5Calculator and Sha256Calculator.
 */
package checker.core;

import java.io.IOException;
import java.io.InputStream;

public interface ChecksumCalculator {
    String calculate(InputStream input) throws IOException;
}
