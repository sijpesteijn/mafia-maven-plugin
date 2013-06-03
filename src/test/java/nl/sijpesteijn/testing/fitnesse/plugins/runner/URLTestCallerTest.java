package nl.sijpesteijn.testing.fitnesse.plugins.runner;

import fitnesse.wiki.PageType;
import nl.sijpesteijn.testing.fitnesse.plugins.AbstractFitNesseTest;
import nl.sijpesteijn.testing.fitnesse.plugins.report.MafiaTestSummary;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * URL test caller test.
 */
public class URLTestCallerTest extends AbstractFitNesseTest {
    private FitNesseCommander commander = new FitNesseCommander(getFitnesseCommanderConfig());
    private ResultStore resultStoreMock;

    @Before
    public void setup() throws Throwable {
        resultStoreMock = mock(ResultStore.class);
        commander.start();
        Thread.sleep(5000);
    }

    @After
    public void tearDown() throws Throwable {
        commander.stop();
    }

    @Test
    public void testSuccess() throws Exception {
        URLTestCaller testCaller = new URLTestCaller(PORT, "http", "localhost",new File(TEST_RESULT_DIR),
                resultStoreMock);
        when(resultStoreMock.saveResult(isA(String.class), isA(File.class), isA(String.class))).thenReturn(new
                MafiaTestSummary());
        final String testName =
                "FitNesse.SuiteAcceptanceTests.SuiteWidgetTests.TestExpression";
        MafiaTestSummary mafiaTestSummary = testCaller.test(testName, PageType.TEST, null, "/tests/");
        assertNotNull(mafiaTestSummary);
    }

    @Test
    public void testNoConnection() throws Exception {
        URLTestCaller testCaller = new URLTestCaller(PORT, "http", "wronghost",new File(TEST_RESULT_DIR),
                resultStoreMock);
        when(resultStoreMock.saveResult(isA(String.class), isA(File.class), isA(String.class))).thenReturn(new
                MafiaTestSummary());
        final String testName =
                "FitNesse.SuiteAcceptanceTests.SuiteWidgetTests.TestExpression";
        try {
            MafiaTestSummary mafiaTestSummary = testCaller.test(testName, PageType.TEST, null, "/tests/");
            assertNotNull(mafiaTestSummary);
        } catch(MafiaException me) {
            assertEquals("Could not open connection.", me.getMessage());
        }
    }

    @Test
    public void testMailFormattedUrl() throws Exception {
        URLTestCaller testCaller = new URLTestCaller(PORT, "brrr", "localhost",new File(TEST_RESULT_DIR),
                resultStoreMock);
        when(resultStoreMock.saveResult(isA(String.class), isA(File.class), isA(String.class))).thenReturn(new
                MafiaTestSummary());
        final String testName =
                "FitNesse.SuiteAcceptanceTests.SuiteWidgetTests.TestExpression";
        try {
            MafiaTestSummary mafiaTestSummary = testCaller.test(testName, PageType.TEST, null, "/tests/");
            assertNotNull(mafiaTestSummary);
        } catch(MafiaException me) {
            assertEquals("Could not make url call.", me.getMessage());
        }
    }
}
