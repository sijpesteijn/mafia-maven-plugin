package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import java.io.InputStream;

/**
 * Monitor a stream.
 * 
 */
public class InputStreamToBufferMonitor implements Runnable {

	private final InputStream inputStream;
	private final StringBuilder buffer;
	private boolean finished;

	public InputStreamToBufferMonitor(final InputStream inputStream, final StringBuilder buffer) {
		this.inputStream = inputStream;
		this.buffer = buffer;
	}

	@Override
	public void run() {
		try {
			int c;
			while ((c = inputStream.read()) != -1 && inputStream.available() > 0) {
				buffer.append((char) c);
			}
			finished = true;
		} catch (final Exception e) {

		}
	}

	public boolean isFinished() {
		return finished;
	}

	public StringBuilder getBuffer() {
		return buffer;
	}

}
