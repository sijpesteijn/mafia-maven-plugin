package nl.sijpesteijn.testing.fitnesse.plugins.report;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static junit.framework.Assert.assertNotNull;

/**
 * User: gijs
 * Date: 5/19/13 11:37 PM
 */
public class JsoupReportFormatterTest extends AbstractReportTest {
    private JsoupReportFormatter jsoupReportFormatter;

    @Before
    public void setup() throws Throwable {
        jsoupReportFormatter = new JsoupReportFormatter();
    }

    @Test
    public void testTestReport() throws Throwable {
        File testFile = new File("./target/classes/");
        String htmlResult = FileUtils.readFileToString(new File("./target/test-classes/sampleResults/tests/FitNesse.SuiteAcceptanceTests.SuiteVirtualWikiTests.TestAccessVirtualChild.html"));
        MafiaTestResult mafiaTestSummary = new MafiaTestResult(testFile, getSummary(0,1,1,0), htmlResult);
        String html = jsoupReportFormatter.getHtml(mafiaTestSummary, TestType.TEST);
        assertNotNull(html);
    }

    @Test
    public void testSuiteReport() throws Throwable {
        File testFile = new File("./target/classes/");
        String htmlResult = FileUtils.readFileToString(new File("./target/test-classes/sampleResults/suites/FitNesse.SuiteAcceptanceTests.SuiteVirtualWikiTests.html"));
        MafiaTestResult mafiaTestSummary = new MafiaTestResult(testFile, getSummary(0,0,1,0), htmlResult);
        String html = jsoupReportFormatter.getHtml(mafiaTestSummary, TestType.SUITE);
        assertNotNull(html);
    }


}
