package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Start a process and monitor the std out and std error.
 * 
 */
public class CommandRunner {
    private Process process;
    private final StringBuilder errorBuffer = new StringBuilder();
    private final StringBuilder outputBuffer = new StringBuilder();

    /**
     * Run the command.
     * 
     * @param command
     *        {@link java.lang.String}
     * @throws IOException
     */
    public void start(final String command) throws IOException {
        process = Runtime.getRuntime().exec(command);
    }

    /**
     * Check if the unpacking of FitNesse has finished.
     * 
     * @return {@link boolean}
     * @throws InterruptedException
     */
    public boolean waitForSetupToFinish() throws InterruptedException {
        createStreamMonitors(process);
        while (true) {
            if (errorBufferHasContent()) {
                if (!bufferContains(errorBuffer, "patient.")) {
                    // log.error(errorBuffer.toString());
                    return false;
                } else {
                    // log.info("Unpacking....");
                }
            }
            if (bufferContains(outputBuffer, "days.")) {
                return true;
            }
            Thread.sleep(2000);
        }
    }

    /**
     * Create the stream monitors.
     * 
     * @param process
     *        {@link java.lang.Process}
     */
    private void createStreamMonitors(final Process process) {
        final InputStream errorStream = process.getErrorStream();
        new Thread(new StreamToBufferMonitor(errorStream, errorBuffer)).start();
        final InputStream outputStream = process.getInputStream();
        new Thread(new StreamToBufferMonitor(outputStream, outputBuffer)).start();
    }

    /**
     * Check if the buffer contains the specified string.
     * 
     * @param buffer
     *        {@link java.lang.StringBuilder}
     * @param string
     *        {@link java.lang.String}
     * @return {@link boolean}
     */
    private boolean bufferContains(final StringBuilder buffer, final String string) {
        return buffer.toString().indexOf(string) > 0;
    }

    /**
     * Is the error buffer empty?
     * 
     * @return {@link boolean}
     */
    public boolean errorBufferHasContent() {
        return errorBuffer.length() > 0;
    }

    /**
     * Check if the error buffer contains the specified string.
     * 
     * @param string
     *        {@link java.lang.String}
     * @return {@link boolean}
     */
    public boolean errorBufferContains(final String string) {
        return bufferContains(errorBuffer, string);
    }

    /**
     * Get the error buffer contents.
     * 
     * @return {@link java.lang.String}
     */
    public String getErrorBufferMessage() {
        return errorBuffer.toString();
    }

    /**
     * Return the output buffer contents.
     * 
     * @return {@link java.lang.CharSequence}
     */
    public CharSequence getOutputBuffer() {
        return outputBuffer;
    }
}
