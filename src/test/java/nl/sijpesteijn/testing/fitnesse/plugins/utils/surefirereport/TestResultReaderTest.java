package nl.sijpesteijn.testing.fitnesse.plugins.utils.surefirereport;

import static junit.framework.Assert.*;

import java.io.File;
import java.util.List;

import nl.sijpesteijn.testing.fitnesse.plugins.utils.surefirereport.TestResult;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.surefirereport.TestResultReader;

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
			.withRunTimeInMillis(903);

		File file = new File(TEST_RESULT_BASE_FOLDER + "/Test1/20150408111924_0_1_0_0.xml");
		TestResult testResult = testResultReader.readTestResultFile(file);
		assertEquals(expectedTestResult, testResult);
	}

	/**
	 * don't use suites and only use latest xml file in a folder.
	 */
	@Test
	public void readAllTestResultFiles() {
		File baseFolder = new File(TEST_RESULT_BASE_FOLDER);
		List<TestResult> testResults = testResultReader.readAllTestResultFiles(baseFolder);
		assertEquals(4, testResults.size());
	}

}
