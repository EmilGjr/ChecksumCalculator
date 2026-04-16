/**
 * This class wraps an InputStream and reports progress while reading data.
 * It is used to track progress during checksum calculation for large files.
 */
package checker.control;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.LongConsumer;

public class ProgressInputStream extends FilterInputStream {

    private final LongConsumer progressListener;

    public ProgressInputStream(InputStream in, LongConsumer progressListener) {
        super(in);
        this.progressListener = progressListener;
    }

    @Override
    public int read() throws IOException {
        int result = super.read();
        if (result != -1) {
            progressListener.accept(1L);
        }
        return result;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int count = super.read(b, off, len);
        if (count > 0) {
            progressListener.accept(count);
        }
        return count;
    }

    @Override
    public long skip(long n) throws IOException {
        long skipped = super.skip(n);
        if (skipped > 0) {
            progressListener.accept(skipped);
        }
        return skipped;
    }
}
