package nl.sijpesteijn.testing.fitnesse.plugins.utils.surefirereport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * read TestResults objects from xml files in FitNesseRoot/files/testResults/<testname>
 */
public class TestResultReader {

    private Log log;

    public TestResultReader(Log log) {
        this.log = log;
    }

    @SuppressWarnings("unchecked")
    public List<TestResult> readAllTestResultFiles(File baseFolder) {
        if (!baseFolder.exists()) {
            log.info("Could not read Test Results. Folder " + baseFolder
                + " doesn't exist. Probably no tests have been executed.");
            return Collections.EMPTY_LIST;
        }
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
            Element rootElement = getRootXmlElement(testResultFile);
            if (!rootElement.getNodeName().equals("testResults")) {
                return null;
            }
            TestResult testResult = createBeanWithBaseInfo(rootElement);

            Element executionLogNode = (Element) rootElement.getElementsByTagName("executionLog").item(0);
            if (executionLogNode != null) {
                fillBeanWithExecutionLogInfo(testResult, executionLogNode);
            }
            Element resultNode = (Element) rootElement.getElementsByTagName("result").item(0);
            if (resultNode != null) {
                fillBeanWithResults(testResult, resultNode);
            }
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

    private TestResult createBeanWithBaseInfo(Element rootElement) {
        String path = rootElement.getElementsByTagName("rootPath").item(0).getTextContent();
        String runTimeString = rootElement.getElementsByTagName("totalRunTimeInMillis").item(0).getTextContent();
        TestResult testResult = new TestResult()
            .withPath(path)
            .withRunTimeInMillis(Long.parseLong(runTimeString));
        return testResult;
    }

    private void fillBeanWithExecutionLogInfo(TestResult testResult, Element executionLogNode) {
        String exitCode = executionLogNode.getElementsByTagName("exitCode").item(0).getTextContent();
        testResult.withExitCode(Integer.parseInt(exitCode));
        Node exceptionNode = executionLogNode.getElementsByTagName("exception").item(0);
        if (exceptionNode != null) {
            String exceptionText = exceptionNode.getTextContent();
            if (!exceptionText.trim().isEmpty()) {
                testResult.withExecutionLogException(exceptionText)
                    .withExceptionCount(1);
            }
        }
    }

    private void fillBeanWithResults(TestResult testResult, Element resultNode) throws XPathExpressionException {
        Element countsNode = (Element) resultNode.getElementsByTagName("counts").item(0);
        String rightCount = countsNode.getElementsByTagName("right").item(0).getTextContent();
        String wrongCount = countsNode.getElementsByTagName("wrong").item(0).getTextContent();
        String ignoresCount = countsNode.getElementsByTagName("ignores").item(0).getTextContent();
        String exceptionsCount = countsNode.getElementsByTagName("exceptions").item(0).getTextContent();

        testResult.withRightTestCount(Integer.parseInt(rightCount))
            .withWrongTestCount(Integer.parseInt(wrongCount))
            .withIgnoredTestCount(Integer.parseInt(ignoresCount))
            .withExceptionCount(Integer.parseInt(exceptionsCount));
    }

    private Element getRootXmlElement(File testResultFile) throws ParserConfigurationException, SAXException,
        IOException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(testResultFile);
        Element rootElement = document.getDocumentElement();
        return rootElement;
    }

    private TestResultException fireException(File testResultFile, Exception e) {
        throw new TestResultException("Couldn't create TestResult from file " + testResultFile, e);
    }
}
