package nl.sijpesteijn.testing.fitnesse.plugins.report;

import nl.sijpesteijn.testing.fitnesse.plugins.FitNesseReportMojo;
import org.apache.commons.io.FileUtils;
import org.apache.maven.doxia.sink.Sink;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: gijs
 * Date: 5/20/13 10:08 AM
 */
public class MafiaIndexGeneratorTest extends AbstractReportTest {
    private MafiaIndexGenerator mafiaIndexGenerator;
    private ResourceBundle resourceBundleMock;
    private TestResultRepository testResultRepositoryMock;

    @Before
    public void setup() throws Throwable {
        testResultRepositoryMock = mock(TestResultRepository.class);
        resourceBundleMock = ResourceBundle.getBundle("mafia-report", Locale.getDefault(), FitNesseReportMojo.class.getClassLoader()); // mock(ResourceBundle.class);
        Sink sinkMock = mock(Sink.class);
        MafiaTestSummary mafiaTestSummary = getSummary(1,0,0,0);
        ReportFormatter reportFormatterMock = mock(ReportFormatter.class);
        mafiaIndexGenerator = new MafiaIndexGenerator(testResultRepositoryMock, resourceBundleMock, sinkMock, mafiaTestSummary, reportFormatterMock);
    }

    @Test
    public void testGenerate() throws Exception {
        List<MafiaTestResult> testResults = getTestResults();
        when(testResultRepositoryMock.getTestResults()).thenReturn(testResults);
        when(testResultRepositoryMock.getSuitesResults()).thenReturn(testResults);
        when(testResultRepositoryMock.getFilteredSuitesResults()).thenReturn(testResults);
        mafiaIndexGenerator.generate();
    }

    @Test
    public void testGetTime() throws Exception {
        mafiaIndexGenerator.getTime(1234567890l);
    }

    public List<MafiaTestResult> getTestResults() throws IOException {
        List<MafiaTestResult> testResults = new ArrayList<MafiaTestResult>();
        File testFile = new File("./target/classes/");
        String suiteHtmlResult = FileUtils.readFileToString(new File("./target/test-classes/sampleResults/tests/FitNesse.SuiteAcceptanceTests.SuiteVirtualWikiTests.TestAccessVirtualChild.html"));
        MafiaTestResult mafiaSuiteSummary = new MafiaTestResult(testFile, getSummary(0,1,0,0), suiteHtmlResult);
        testResults.add(mafiaSuiteSummary);
        String testHtmlResult = FileUtils.readFileToString(new File("./target/test-classes/sampleResults/tests/FitNesse.SuiteAcceptanceTests.SuiteVirtualWikiTests.TestAccessVirtualChild.html"));
        MafiaTestResult mafiaTestSummary = new MafiaTestResult(testFile, getSummary(0,0,1,1), testHtmlResult);
        testResults.add(mafiaTestSummary);

        return testResults;
    }
}
