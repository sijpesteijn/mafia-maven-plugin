package nl.sijpesteijn.testing.fitnesse.plugins.utils.surefirereport;

import static junit.framework.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.testing.SilentLog;
import org.junit.Before;
import org.junit.Test;

public class SurefireReportWriterTest {

    private static final String EXPRECTED_SUREFIRE_REPORTS_DIR = "src/test/resources/expected-surefire-reports/";
    private static final String ACTUAL_SUREFIRE_REPORTS_DIR = "target/testResources/actual-surefire-reports/";
    private SurefireReportWriter surefireReportWriter;
    private File serializedReportFolder;

    @Before
    public void init() {
        surefireReportWriter = new SurefireReportWriter(new SilentLog(), "dummypath/", "dummypath2/");
        serializedReportFolder = new File(ACTUAL_SUREFIRE_REPORTS_DIR);
        serializedReportFolder.mkdirs();
    }


    @Test
    public void serializeSingleFile() throws FileNotFoundException, IOException {
        TestResult expectedTestResult = new TestResult()
            .withPath("Suite1.Suite11.Test1")
            .withRightTestCount(5)
            .withWrongTestCount(4)
            .withIgnoredTestCount(1)
            .withExceptionCount(1)
            .withRunTimeInMillis(300);

        File serializedReport = new File(serializedReportFolder, "report1.xml");
        surefireReportWriter.serialize(expectedTestResult, serializedReport);

        List<String> serializedReportContent = IOUtils.readLines(new FileReader(serializedReport));
        File expectedReport = new File(EXPRECTED_SUREFIRE_REPORTS_DIR, "report1.xml");
        List<String> expectedReportContent = IOUtils.readLines(new FileReader(expectedReport));

        assertListEquals(expectedReportContent, serializedReportContent);
    }
    
    @Test
    public void serializeSingleFileWithError() throws FileNotFoundException, IOException {
        TestResult expectedTestResult = new TestResult()
        .withPath("Suite1.Suite11.Test1")
        .withRightTestCount(0)
        .withWrongTestCount(0)
        .withIgnoredTestCount(0)
        .withExceptionCount(1)
        .withRunTimeInMillis(300)
        .withExitCode(143)
        .withExcutionLogException("Read timed out");
        
        File serializedReport = new File(serializedReportFolder, "report2-exception.xml");
        surefireReportWriter.serialize(expectedTestResult, serializedReport);
        
        List<String> serializedReportContent = IOUtils.readLines(new FileReader(serializedReport));
        File expectedReport = new File(EXPRECTED_SUREFIRE_REPORTS_DIR, "report2-exception.xml");
        List<String> expectedReportContent = IOUtils.readLines(new FileReader(expectedReport));
        
        assertListEquals(serializedReportContent, expectedReportContent);
    }


    private void assertListEquals(List<String> serializedReportContent, List<String> expectedReportContent) {
        String joinedExpected = StringUtils.join(expectedReportContent, "\n");
        String joinedActual = StringUtils.join(serializedReportContent, "\n");
        assertEquals(joinedExpected, joinedActual);
    }
    
    @Test
    public void serializeManyFiles() throws FileNotFoundException, IOException {
        List<TestResult> testResults = createDummyTestResults();

        File surefireReportsFolder = new File("target/testResources/actual-surefire-reports-many");
        surefireReportsFolder.mkdirs();

        surefireReportWriter.serialize(testResults, surefireReportsFolder);

        long fileCount = surefireReportsFolder.list().length;
        assertEquals(2, fileCount);

        List<String> createdReportFiles = Arrays.asList(surefireReportsFolder.list());
        assertTrue(createdReportFiles.contains("TEST-Suite1.Suite11.Test1.xml"));
        assertTrue(createdReportFiles.contains("TEST-Suite2.Suite21.Test2.xml"));
    }

    private List<TestResult> createDummyTestResults() {
        TestResult expectedTestResult = new TestResult()
            .withPath("Suite1.Suite11.Test1")
            .withRightTestCount(5)
            .withWrongTestCount(4)
            .withIgnoredTestCount(1)
            .withExceptionCount(1)
            .withRunTimeInMillis(300);
        TestResult expectedTestResult2 = new TestResult()
            .withPath("Suite2.Suite21.Test2")
            .withRightTestCount(3)
            .withWrongTestCount(2)
            .withIgnoredTestCount(2)
            .withExceptionCount(5)
            .withRunTimeInMillis(500);
        List<TestResult> input = new ArrayList<TestResult>();
        input.add(expectedTestResult);
        input.add(expectedTestResult2);
        return input;
    }
    
}
