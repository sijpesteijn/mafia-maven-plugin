package nl.sijpesteijn.testing.fitnesse.plugins.utils.surefirereport;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.logging.Log;

/**
 * writes TestResults objects to /surefire-reports/ folder in the testsuite xml format.
 */
public class SurefireReportWriter {

	private Log log;

	private String mafiaResultsDir;

	public SurefireReportWriter(Log log, String mafiaResultsDir) {
		this.log = log;
		this.mafiaResultsDir = mafiaResultsDir;
	}

	final static String SUREFIRE_REPORT_TEMPLATE =
		"<testsuite errors=\"%s\" skipped=\"%s\" tests=\"%s\" time=\"%s\" failures=\"%s\" name=\"%s\">%n"
			+ "<properties/>%n"
			+ "<testcase classname=\"%s\" time=\"%s\" name=\"%s\">%n"
			+ "%s%n"
			+ "</testcase>%n"
			+ "</testsuite>%n";

	final static String SUREFIRE_REPORT_ERROR_PART_TEMPLATE =
		"<error type=\"java.lang.AssertionError\" message=\"exceptions: %s wrong: %s\"><br/>"
			+ "See %s in workspace for more details."
			+ "</error>";

	public void serialize(List<TestResult> testResults, File surefireReportBaseDir) {
		for (TestResult testResult : testResults) {
			String targetFileName = "TEST-" + testResult.getPath() + ".xml";
			File targetFile = new File(surefireReportBaseDir, targetFileName);
			try {
				serialize(testResult, targetFile);
			} catch (TestResultException ex) {
				log.error(ex.getMessage());
				log.debug(ex);
			}
		}
	}

	void serialize(TestResult testResult, File targetFile) throws TestResultException {
		String errorXmlPart = createErrorXmlPart(testResult);
		String surefireReportContent = String.format(SUREFIRE_REPORT_TEMPLATE,
			testResult.getExceptionCount(),
			testResult.getIgnoredTestCount(),
			testResult.getTotalTestCount(),
			testResult.getRunTimeInSec(),
			testResult.getWrongTestCount(),
			testResult.getSuiteName(),
			testResult.getPath(),
			testResult.getRunTimeInSec(),
			testResult.getTestName(),
			errorXmlPart
			);
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(targetFile);
			IOUtils.write(surefireReportContent, fileWriter);
			fileWriter.flush();
		} catch (IOException e) {
			throw fireException(targetFile, e);
		} finally {
			IOUtils.closeQuietly(fileWriter);
		}
	}

	private String createErrorXmlPart(TestResult testResult) {
		boolean noTestFailures = testResult.getExceptionCount() + testResult.getWrongTestCount() == 0;
		if (noTestFailures) {
			return "";
		}
		String pathToHtmlFile = mafiaResultsDir + "tests/" + testResult.getPath() + ".html";
		String surefireReportContent = String.format(SUREFIRE_REPORT_ERROR_PART_TEMPLATE,
			testResult.getExceptionCount(),
			testResult.getWrongTestCount(),
			pathToHtmlFile
			);
		return surefireReportContent;
	}

	private TestResultException fireException(File targetFile, Exception e) {
		throw new TestResultException("Couldn't write TestResult to surefire report xml in " + targetFile, e);
	}
}
