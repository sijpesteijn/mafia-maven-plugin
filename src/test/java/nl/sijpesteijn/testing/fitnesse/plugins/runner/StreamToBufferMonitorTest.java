package nl.sijpesteijn.testing.fitnesse.plugins.runner;

import org.junit.Test;

/**
 * User: gijs
 * Date: 5/19/13 6:51 PM
 */
public class StreamToBufferMonitorTest {

    @Test
    public void testException() throws Exception {
        StreamToBufferMonitor streamToBufferMonitor = new StreamToBufferMonitor(null);
        streamToBufferMonitor.run();
    }
}
