package nl.sijpesteijn.testing.fitnesse.plugins.utils.surefirereport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * read TestResults objects from xml files in FitNesseRoot/files/testResults/<testname>
 */
public class TestResultReader {

	private Log log;

	public TestResultReader(Log log) {
		this.log = log;
	}

	public List<TestResult> readAllTestResultFiles(File baseFolder) {
		List<File> testResultFiles = getLatestTestResultFiles(baseFolder);
		List<TestResult> testResults = new ArrayList<TestResult>(testResultFiles.size());
		for (File testResultFile : testResultFiles) {
			try {
				TestResult testResult = readTestResultFile(testResultFile);
				boolean isValidTestResults = testResult != null;
				if (isValidTestResults) {
					testResults.add(testResult);
				}
			} catch (TestResultException ex) {
				log.error(ex.getMessage());
				log.debug(ex);
			}
		}
		return testResults;
	}

	private List<File> getLatestTestResultFiles(File baseFolder) {
		File[] testFolders = baseFolder.listFiles();
		List<File> result = new ArrayList<File>(testFolders.length);
		for (File testFolder : testFolders) {
			result.add(getLatestTestResultFile(testFolder));
		}
		return result;
	}

	File getLatestTestResultFile(File testFolder) {
		File[] listFiles = testFolder.listFiles();
		Arrays.sort(listFiles);
		File file = listFiles[listFiles.length - 1];
		return file;
	}

	TestResult readTestResultFile(File testResultFile) throws TestResultException {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = builder.parse(testResultFile);
			Element rootElement = document.getDocumentElement();
			if (!rootElement.getNodeName().equals("testResults")) {
				return null;
			}

			XPath xpath = XPathFactory.newInstance().newXPath();
			Element countsNode = (Element) xpath.evaluate("result/counts", rootElement, XPathConstants.NODE);
			String rightCount = countsNode.getElementsByTagName("right").item(0).getTextContent();
			String wrongCount = countsNode.getElementsByTagName("wrong").item(0).getTextContent();
			String ignoresCount = countsNode.getElementsByTagName("ignores").item(0).getTextContent();
			String exceptionsCount = countsNode.getElementsByTagName("exceptions").item(0).getTextContent();

			String runTimeString = (String) xpath.evaluate("result/runTimeInMillis/text()", rootElement,
				XPathConstants.STRING);

			String path = (String) xpath.evaluate("rootPath", rootElement, XPathConstants.STRING);

			TestResult testResult = new TestResult()
				.withPath(path)
				.withRightTestCount(Integer.parseInt(rightCount))
				.withWrongTestCount(Integer.parseInt(wrongCount))
				.withIgnoredTestCount(Integer.parseInt(ignoresCount))
				.withExceptionCount(Integer.parseInt(exceptionsCount))
				.withRunTimeInMillis(Long.parseLong(runTimeString));
			return testResult;
		} catch (ParserConfigurationException e) {
			throw fireException(testResultFile, e);
		} catch (SAXException e) {
			throw fireException(testResultFile, e);
		} catch (IOException e) {
			throw fireException(testResultFile, e);
		} catch (XPathExpressionException e) {
			throw fireException(testResultFile, e);
		}
	}

	private TestResultException fireException(File testResultFile, Exception e) {
		throw new TestResultException("Couldn't create TestResult from file " + testResultFile, e);
	}
}
