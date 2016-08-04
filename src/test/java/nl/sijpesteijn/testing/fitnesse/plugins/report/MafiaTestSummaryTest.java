package nl.sijpesteijn.testing.fitnesse.plugins.report;

import org.junit.Before;
import org.junit.Test;

import fitnesse.wiki.PageType;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

/**
 * User: gijs
 * Date: 5/20/13 10:43 AM
 */
public class MafiaTestSummaryTest {
    private MafiaTestSummary summary;

    @Before
    public void setup() throws Exception {
        summary = new MafiaTestSummary(0,0,0,0);
        summary.setPageType(PageType.SUITE);
        summary.setWikiPage("wikiName");
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
        assertTrue(reportFileName.contains(PageType.SUITE.toString().toLowerCase()));
        assertEquals("suites"+File.separator+"wikiName"+File.separator+"fitnesse-results.xml", reportFileName);
    }

    @Test
    public void testTestTime() throws Exception {
        summary.setTestTime(10L);
        assertTrue(summary.getTestTime() == 10L);
    }

    @Test
    public void testRunDate() throws Exception {
        long runDate = 0l;
        summary.setRunDate(runDate);
        assertEquals(runDate, summary.getRunDate());
    }

    @Test
    public void testToString() throws Exception {
        String toString = summary.toString();
        assertTrue(toString.contains(", file: "));
        assertTrue(toString.endsWith("suites"+File.separator+"wikiName"+File.separator+"fitnesse-results.xml"));
    }
}
