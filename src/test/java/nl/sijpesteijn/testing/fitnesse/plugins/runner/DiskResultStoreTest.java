package nl.sijpesteijn.testing.fitnesse.plugins.runner;

import fitnesse.wiki.PageType;
import nl.sijpesteijn.testing.fitnesse.plugins.report.MafiaTestSummary;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * User: gijs
 * Date: 5/19/13 9:25 AM
 */
public class DiskResultStoreTest {
    private static final String CONTENT = "<testResults><finalCounts><right>10</right><wrong>0</wrong><ignores>0</ignores><exceptions>34</exceptions></finalCounts></testResults>";
    private DiskResultStore store;

    @Before
    public void setup() throws Exception {
        store = new DiskResultStore();
    }

    @Test
    public void testStore() throws Throwable {
        MafiaTestSummary mafiaTestSummary = store.saveResult(CONTENT, new File("./target/junitTestResults"), 10l, PageType.TEST, "sample-test", null);
        assertNotNull(mafiaTestSummary);
        assertTrue(mafiaTestSummary.getRight() == 10);
    }
}
