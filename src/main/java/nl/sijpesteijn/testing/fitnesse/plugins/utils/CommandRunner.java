package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Start a process and monitor the std out and std error.
 * 
 */
public class CommandRunner {
	private Process process;
	private int exitValue;
	private File workDirectory = null;
	private InputStreamToBufferMonitor inputMonitor;
	private InputStreamToBufferMonitor errorMonitor;

	public CommandRunner(final String workDirectory) {
		if (workDirectory != null)
			this.workDirectory = new File(workDirectory);
	}

	/**
	 * Run the command.
	 * 
	 * @param command
	 *            {@link java.lang.String}
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void start(final String command) throws IOException,
			InterruptedException {
		process = Runtime.getRuntime().exec(command, null, workDirectory);
		waitForSetupToFinish();
	}

	/**
	 * Check if the unpacking of FitNesse has finished.
	 * 
	 * @param endCondition
	 * 
	 * @return {@link boolean}
	 * @throws InterruptedException
	 */
	private void waitForSetupToFinish() throws InterruptedException {
		createStreamMonitors(process);
		while (true) {
			try {
				exitValue = process.exitValue();
				return;
			} catch (final IllegalThreadStateException itse) {
				// Process has not finished yet
			}
			if (inputMonitor.isFinished()) {
				return;
			}
			Thread.sleep(2000);
		}
	}

	/**
	 * Create the stream monitors.
	 * 
	 * @param process
	 *            {@link java.lang.Process}
	 */
	private void createStreamMonitors(final Process process) {
		final InputStream errorStream = process.getErrorStream();
		errorMonitor = new InputStreamToBufferMonitor(errorStream,
				new StringBuilder());
		new Thread(errorMonitor).start();
		final InputStream inputStream = process.getInputStream();
		inputMonitor = new InputStreamToBufferMonitor(inputStream,
				new StringBuilder());
		new Thread(inputMonitor).start();
	}

	/**
	 * Check if the buffer contains the specified string.
	 * 
	 * @param buffer
	 *            {@link java.lang.StringBuilder}
	 * @param string
	 *            {@link java.lang.String}
	 * @return {@link boolean}
	 */
	private boolean bufferContains(final StringBuilder buffer,
			final String string) {
		return buffer.toString().indexOf(string) > 0;
	}

	/**
	 * Is the error buffer empty?
	 * 
	 * @return {@link boolean}
	 */
	public boolean errorBufferHasContent() {
		return errorMonitor.getBuffer().length() > 0;
	}

	/**
	 * Check if the error buffer contains the specified string.
	 * 
	 * @param string
	 *            {@link java.lang.String}
	 * @return {@link boolean}
	 */
	public boolean errorBufferContains(final String string) {
		return bufferContains(errorMonitor.getBuffer(), string);
	}

	/**
	 * Get the error buffer contents.
	 * 
	 * @return {@link java.lang.CharSequence}
	 */
	public CharSequence getErrorBuffer() {
		return errorMonitor.getBuffer();
	}

	public int getExitValue() {
		return exitValue;
	}
}
