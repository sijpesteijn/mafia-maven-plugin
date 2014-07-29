package nl.sijpesteijn.testing.fitnesse.plugins.runner;

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
    private static final String LINE = "<script>document.getElementById(\"test-summary\").innerHTML = \"<strong>Test Pages:</strong> 4 right, 0 wrong, 0 ignored, 0 exceptions&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<strong>Assertions:</strong> 10 right, 0 wrong, 0 ignored, 0 exceptions (1.754 seconds)\";document.getElementById(\"test-summary\").className = \"pass\";</script>";
    private static final String STYLE = "<link rel=\"stylesheet\" type=\"text/css\" href=\"/files/fitnesse/css/fitnesse_wiki.css\">";
    private DiskResultStore store;

    @Before
    public void setup() throws Exception {
        store = new DiskResultStore();
    }

    @Test
    public void testStore() throws Throwable {
        MafiaTestSummary mafiaTestSummary = store.saveResult(LINE + "\n" + STYLE, new File("./target/junitTestResults"), "sample-test");
        assertNotNull(mafiaTestSummary);
        assertTrue(mafiaTestSummary.getRight() == 10);
    }
}
