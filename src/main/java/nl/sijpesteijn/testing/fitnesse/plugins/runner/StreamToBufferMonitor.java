package nl.sijpesteijn.testing.fitnesse.plugins.runner;

import java.io.InputStream;

/**
 * StreamToBufferMonitor.
 */
public class StreamToBufferMonitor implements Runnable {

    /**
     * Input stream to monitor.
     */
    private final InputStream inputStream;

    /**
     * Output buffer.
     */
    private final StringBuilder buffer = new StringBuilder();

    /**
     * Indicates if the process has finished.
     */
    private boolean finished;

    /**
     * Constructor.
     *
     * @param inputStream {@link java.io.InputStream}
     */
    public StreamToBufferMonitor(final InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * Start the monitoring.
     */
    @Override
    @SuppressWarnings("PMD")
    public final void run() {
        try {
            int c = inputStream.read();
            while (c != -1 && inputStream.available() > 0) {
                buffer.append((char) c);
                c = inputStream.read();
            }
            finished = true;
        } catch (final Exception e) {
            // there is no data on the stream. That can happen.
        }
    }

    /**
     * Has the process finished.
     *
     * @return boolean
     */
    public final boolean isFinished() {
        return finished;
    }

    /**
     * The output.
     *
     * @return {@link java.lang.StringBuilder}
     */
    public final StringBuilder getBuffer() {
        return buffer;
    }
}
