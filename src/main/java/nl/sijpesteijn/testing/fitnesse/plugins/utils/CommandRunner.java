package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import java.io.IOException;
import java.io.InputStream;

public class CommandRunner {
    private Process process;
    private final StringBuilder errorBuffer = new StringBuilder();
    private final StringBuilder outputBuffer = new StringBuilder();

    public void start(final String command) throws IOException {
        process = Runtime.getRuntime().exec(command);
    }

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

    private void createStreamMonitors(final Process process) {
        final InputStream errorStream = process.getErrorStream();
        new Thread(new StreamToBufferMonitor(errorStream, errorBuffer)).start();
        final InputStream outputStream = process.getInputStream();
        new Thread(new StreamToBufferMonitor(outputStream, outputBuffer)).start();
    }

    private boolean bufferContains(final StringBuilder buffer, final String string) {
        return buffer.toString().indexOf(string) > 0;
    }

    public boolean errorBufferHasContent() {
        return errorBuffer.length() > 0;
    }

    public boolean errorBufferContains(final String string) {
        return bufferContains(errorBuffer, string);
    }

    public String getErrorBufferMessage() {
        return errorBuffer.toString();
    }

    public CharSequence getOutputBuffer() {
        return outputBuffer;
    }
}
