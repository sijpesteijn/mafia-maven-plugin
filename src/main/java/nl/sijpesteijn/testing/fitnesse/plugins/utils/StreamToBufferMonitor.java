package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import java.io.InputStream;

/**
 * Monitor a stream.
 * 
 */
public class StreamToBufferMonitor implements Runnable {

    private final InputStream inputStream;
    private final StringBuilder buffer;

    public StreamToBufferMonitor(final InputStream inputStream, final StringBuilder buffer) {
        this.inputStream = inputStream;
        this.buffer = buffer;
    }

    @Override
    public void run() {
        try {
            int c;
            while ((c = inputStream.read()) != -1)
                buffer.append((char) c);
        } catch (final Exception e) {

        }
    }

}
