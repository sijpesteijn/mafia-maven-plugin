package nl.sijpesteijn.testing.fitnesse.plugins.runner;

import fitnesse.wiki.PageType;
import nl.sijpesteijn.testing.fitnesse.plugins.AbstractFitNesseTest;
import nl.sijpesteijn.testing.fitnesse.plugins.report.MafiaTestSummary;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaException;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: gijs
 * Date: 5/10/13 6:59 PM
 */
public class FitNesseTestRunnerTest extends AbstractFitNesseTest {
    private FitNesseTestRunner runner;
    private TestCaller testCallerMock;

    @Before
    public void setup() throws Throwable {
        testCallerMock = mock(TestCaller.class);
        runner = new FitNesseTestRunner(testCallerMock, false, false, false, mockLog);
    }

    @Test
    public void testRunTests() throws Throwable {
        final String testName1 = "FitNesse.SuiteAcceptanceTests.SuiteFixtureTests.SuiteColumnFixtureSpec.TestMissingField";
        final String testName2 = "FitNesse.SuiteAcceptanceTests.SuiteFixtureTests.SuiteColumnFixtureSpec" +
                ".TestMissingMethod";
        when(testCallerMock.test(testName1, PageType.TEST, null, "/tests/")).thenReturn(new MafiaTestSummary(0,0,0,0));
        when(testCallerMock.test(testName2, PageType.TEST, null, "/tests/")).thenReturn(new MafiaTestSummary(0,0,0,0));

        runner.runTests(new ArrayList<String>() {{
            add(testName1);
            add(testName2);
        }});
        assertTrue(runner.getTestSummaries().size() == 2);
    }

    @Test
    public void testNoTests() throws Throwable {
        runner.runTests(new ArrayList<String>() {{
        }});
        assertTrue(runner.getTestSummaries().size() == 0);
    }

    @Test
    public void testRunSuites() throws Throwable {
        final String suiteName = "FitNesse.SuiteAcceptanceTests.SuiteFixtureTests.SuiteColumnFixtureSpec";
        when(testCallerMock.test(suiteName, PageType.SUITE, null,
                "/suites/")).thenReturn(new MafiaTestSummary(0,0,0,0));
        runner.runSuites(new ArrayList<String>() {{
            add(suiteName);
        }});
        assertTrue(runner.getTestSummaries().size() == 1);
    }

    @Test
    public void testNoSuites() throws Throwable {
        runner.runSuites(new ArrayList<String>() {{
        }});
        assertTrue(runner.getTestSummaries().size() == 0);
    }

    @Test
    public void testRunFilteredSuites() throws Throwable {
        File file = new File(base + "/target/" + NAME_ROOT_PAGE + "/FitNesse/SuiteAcceptanceTests/SuiteFixtureTests/SuiteColumnFixtureSpec/TestSaveAndRecallSymbol/properties.xml");
        if (!file.exists()) {
            FitNesseCommander commander = new FitNesseCommander(getFitnesseCommanderConfig());
            commander.start();
            commander.stop();
        }
        String contents = FileUtils.readFileToString(file);
        contents = contents.replaceAll("<Suites/>","<Suites>precommit</Suites>");
        contents = contents.replaceAll("<Suites></Suites>","<Suites>precommit</Suites>");
        FileUtils.writeStringToFile(file, contents);
        String suiteName = "FitNesse.SuiteAcceptanceTests.SuiteFixtureTests.SuiteColumnFixtureSpec";
        when(testCallerMock.test(suiteName, PageType.SUITE, "precommit",
                "/filteredSuite/")).thenReturn(new MafiaTestSummary(0,0,0,0));

        runner.runFilteredSuite(suiteName, "precommit");
        assertTrue(runner.getTestSummaries().size() == 1);
    }

    @Test
    public void testNoFilteredSuites() throws Throwable {
        runner.runFilteredSuite("FitNesse.SuiteAcceptanceTests.SuiteFixtureTests.SuiteColumnFixtureSpec", "");
        assertTrue(runner.getTestSummaries().size() == 0);
    }

    @Test
    public void testStopConditions() throws Throwable {
        runner = new FitNesseTestRunner(testCallerMock, true, true, true, mockLog);
        final String testName =
                "FitNesse.SuiteAcceptanceTests.SuiteFixtureTests.SuiteColumnFixtureSpec.TestMissingField";

        MafiaTestSummary summary = null;

        summary = new MafiaTestSummary(0, 0, 1, 0);
        when(testCallerMock.test(testName, PageType.TEST, null, "/tests/")).thenReturn(summary);

        try {
            runner.runTests(new ArrayList<String>() {{
                add(testName);
            }});
        } catch (MafiaException me) {
            assertTrue(me.getMessage().endsWith(" failed with ignore exception."));
        }

        summary = new MafiaTestSummary(0,0,0,1);
        when(testCallerMock.test(testName, PageType.TEST, null, "/tests/")).thenReturn(summary);
        try {
            runner.runTests(new ArrayList<String>() {{
                add(testName);
            }});
        } catch (MafiaException me) {
            assertTrue(me.getMessage().endsWith(" failed with an exception."));
        }

        summary = new MafiaTestSummary(0,1,0,0);
        when(testCallerMock.test(testName, PageType.TEST, null, "/tests/")).thenReturn(summary);
        try {
            runner.runTests(new ArrayList<String>() {{
                add(testName);
            }});
        } catch (MafiaException me) {
            assertTrue(me.getMessage().endsWith(" failed with wrong exception."));
        }
    }

    @Test
    public void testStopConditionsNoExceptions() throws Throwable {
        runner = new FitNesseTestRunner(testCallerMock, false, false, false, mockLog);
        final String testName =
                "FitNesse.SuiteAcceptanceTests.SuiteFixtureTests.SuiteColumnFixtureSpec.TestMissingField";

        MafiaTestSummary summary = null;

        summary = new MafiaTestSummary(0,0,1,0);
        when(testCallerMock.test(testName, PageType.TEST, null, "/tests/")).thenReturn(summary);

            runner.runTests(new ArrayList<String>() {{
                add(testName);
            }});

        summary = new MafiaTestSummary(0,0,0,1);
        when(testCallerMock.test(testName, PageType.TEST, null, "/tests/")).thenReturn(summary);
            runner.runTests(new ArrayList<String>() {{
                add(testName);
            }});

        summary = new MafiaTestSummary(0,1,0,0);
        when(testCallerMock.test(testName, PageType.TEST, null, "/tests/")).thenReturn(summary);
            runner.runTests(new ArrayList<String>() {{
                add(testName);
            }});
    }
}
