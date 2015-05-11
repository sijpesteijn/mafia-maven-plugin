package nl.sijpesteijn.testing.fitnesse.plugins.utils.surefirereport;

import static junit.framework.Assert.*;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.testing.SilentLog;
import org.junit.Before;
import org.junit.Test;

public class TestResultReaderTest {

	private static final String TEST_RESULT_BASE_FOLDER = "src/test/resources/testResults";
	private TestResultReader testResultReader;

	@Before
	public void init() {
		testResultReader = new TestResultReader(new SilentLog());
	}

	@Test
	public void getLatestTestResult() {
		File testFolder = new File(TEST_RESULT_BASE_FOLDER + "/Tests4-Many");
		File latestTestResult = testResultReader.getLatestTestResultFile(testFolder);
		assertEquals("20150408134843_0_1_0_0.xml", latestTestResult.getName());
	}

	@Test
	public void readTestResultFile() {
		TestResult expectedTestResult = new TestResult()
			.withPath("Test1")
			.withRightTestCount(1)
			.withWrongTestCount(46)
			.withIgnoredTestCount(10)
			.withExceptionCount(1)
			.withRunTimeInMillis(903)
			.withExitCode(0)
			.withExecutionLogException(null);

		File file = new File(TEST_RESULT_BASE_FOLDER + "/Test1/20150408111924_0_1_0_0.xml");
		TestResult testResult = testResultReader.readTestResultFile(file);
		assertTrue(testResult.executedSuccessfully());
		assertEquals(expectedTestResult, testResult);
	}


	/**
	 * don't use suites and only use latest xml file in a folder.
	 */
	@Test
	public void readAllTestResultFiles() {
		File baseFolder = new File(TEST_RESULT_BASE_FOLDER);
		List<TestResult> testResults = testResultReader.readAllTestResultFiles(baseFolder);
		assertEquals(6, testResults.size());
	}
	
	@Test
	public void fileWithException(){
	    File file = new File(TEST_RESULT_BASE_FOLDER + "/Test5-exception/20150423141746_0_0_0_0.xml");
	    TestResult testResult = testResultReader.readTestResultFile(file);
	    assertFalse(testResult.executedSuccessfully());
	    assertEquals(1, testResult.getExceptionCount());
	    assertEquals(143, (int)testResult.getExitCode());
	    assertEquals("Read timed out", testResult.getExecutionLogException());
	}
	
	   
    @Test
     public void fileWithoutExecutionLog() {
        File file = new File(TEST_RESULT_BASE_FOLDER + "/Test6-noExecLog/20150428121832_1_0_0_0.xml");
        TestResult testResult = testResultReader.readTestResultFile(file);
        assertTrue(testResult.executedSuccessfully());
        assertEquals(3, testResult.getRightTestCount());
        assertEquals(0, testResult.getWrongTestCount());
        assertEquals(0, testResult.getIgnoredTestCount());
        assertEquals(0, testResult.getExceptionCount());
        assertEquals(625, testResult.getRunTimeInMillis());
        assertEquals(null, testResult.getExitCode());
        assertEquals(null, testResult.getExecutionLogException());
    }

}
