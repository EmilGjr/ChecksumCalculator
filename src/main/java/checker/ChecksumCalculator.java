package checker;

import java.io.InputStream;

public interface ChecksumCalculator {
    String calculate(InputStream is);
}