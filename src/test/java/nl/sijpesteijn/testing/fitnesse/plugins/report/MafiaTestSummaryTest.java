package nl.sijpesteijn.testing.fitnesse.plugins.report;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * User: gijs
 * Date: 5/20/13 10:43 AM
 */
public class MafiaTestSummaryTest {
    private MafiaTestSummary summary;

    @Before
    public void setup() throws Exception {
        summary = new MafiaTestSummary();
    }

    @Test
    public void testTimestamp() throws Exception {
        summary.updateReportTimeStamp();
        String timeStamp = summary.getTimeStamp();
        assertNotNull(timeStamp);
    }

    @Test
    public void testGetReportFileName() throws Exception {
        String reportFileName = summary.getReportFileName();
        assertTrue(reportFileName.endsWith("_0_0_0_0.xml"));
    }

    @Test
    public void testTestTime() throws Exception {
        summary.setTestTime(10L);
        assertTrue(summary.getTestTime() == 10L);
    }

    @Test
    public void testRunDate() throws Exception {
        String runDate = "now";
        summary.setRunDate(runDate);
        assertEquals(runDate, summary.getRunDate());
    }

    @Test
    public void testToString() throws Exception {
        String toString = summary.toString();
        assertTrue(toString.contains(", file: "));
        assertTrue(toString.endsWith("_0_0_0_0.xml"));
    }
}
