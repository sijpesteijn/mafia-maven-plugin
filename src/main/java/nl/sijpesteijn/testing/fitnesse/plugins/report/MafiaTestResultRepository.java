package nl.sijpesteijn.testing.fitnesse.plugins.report;

import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * MafiaTestResultRepository.
 */
public class MafiaTestResultRepository implements TestResultRepository {

    /**
     * Start test anchor in test result.
     */
    public static final String START_TESTSUMMARY =
            "<script>document.getElementById(\"test-summary\").innerHTML = \"<strong>Assertions:</strong>";

    /**
     * Start suite anchor in test result.
     */
    public static final String START_SUITESUMMARY = "&nbsp;<strong>Assertions:</strong>";

    /**
     * Test results.
     */
    private final List<MafiaTestResult> testResults;

    /**
     * Suite results.
     */
    private final List<MafiaTestResult> suitesResults;

    /**
     * Filtered suite results.
     */
    private final List<MafiaTestResult> filteredSuitesResults;

    /**
     * Constructor.
     *
     * @param outputDirectory {@link java.io.File}
     * @throws MafiaException thrown in case of an error.
     */
    public MafiaTestResultRepository(final File outputDirectory) throws MafiaException {
        testResults = getTestsFiles(outputDirectory, "tests");
        suitesResults = getTestsFiles(outputDirectory, "suites");
        filteredSuitesResults = getTestsFiles(outputDirectory, "filteredSuite");
    }

    /**
     * Get the test files from the output directory.
     *
     * @param outputDirectory - root output directory.
     * @param sub - sub directory.
     * @return - list of mafia test results.
     * @throws MafiaException - unable to read test files.
     */
    private List<MafiaTestResult> getTestsFiles(final File outputDirectory, final String sub) throws MafiaException {
        List<MafiaTestResult> testDetails = new ArrayList<MafiaTestResult>();
        File testDir = new File(outputDirectory, sub);
        if (testDir != null && testDir.isDirectory()) {
            File[] tests = testDir.listFiles();
            for (File test : tests) {
                try {
                    String html = FileUtils.readFileToString(test);
                    MafiaTestResult testResult = new MafiaTestResult(test, getTestSummary(html), html);
                    testDetails.add(testResult);
                } catch (IOException e) {
                    throw new MafiaException("Could not make mafia test result.", e);
                }
            }
        }
        return testDetails;
    }

    /**
     * Get a mafia test summary from the html.
     *
     * @param html - the html.
     * @return - mafia test summary.
     */
    private MafiaTestSummary getTestSummary(final String html) {
        int start = html.indexOf(START_TESTSUMMARY);
        if (start > 0) {
            int end = html.indexOf("\"", start + START_TESTSUMMARY.length());
            String testSummary = html.substring(start + START_TESTSUMMARY.length(), end);
            return extractSummary(testSummary);
        } else {
            start = html.indexOf(START_SUITESUMMARY);
            if (start > 0) {
                int end = html.indexOf("\"", start + START_SUITESUMMARY.length());
                String suiteSummary = html.substring(start + START_SUITESUMMARY.length(), end);
                return extractSummary(suiteSummary);
            }
        }
        return new MafiaTestSummary();
    }

    /**
     * Extract mafia test summary.
     *
     * @param summary - summary string.
     * @return - mafia test summary.
     */
    private MafiaTestSummary extractSummary(final String summary) {
        String[] parts = summary.split(",");
        String right = parts[0].trim().split(" ")[0].trim();
        String wrong = parts[1].split(" ")[1].trim();
        String ignore = parts[2].split(" ")[1].trim();
        String exception = parts[3].split(" ")[1].trim();
        String time = summary.substring(summary.lastIndexOf("(") + 1, summary.lastIndexOf(")"));
        time = time.replace(".","");
        time = time.replace(",","");
        time = time.split(" ")[0];
        MafiaTestSummary testSummary = new MafiaTestSummary();
        testSummary.right = Integer.parseInt(right);
        testSummary.wrong = Integer.parseInt(wrong);
        testSummary.ignores = Integer.parseInt(ignore);
        testSummary.exceptions = Integer.parseInt(exception);
        testSummary.setTestTime(Long.parseLong(time));
        return testSummary;
    }

    /**
     *
     * {@inheritDoc}
     */
    public final List<MafiaTestResult> getTestResults() {
        return testResults;
    }

    /**
     *
     * {@inheritDoc}
     */
    public final List<MafiaTestResult> getSuitesResults() {
        return suitesResults;
    }

    /**
     *
     * {@inheritDoc}
     */
    public final List<MafiaTestResult> getFilteredSuitesResults() {
        return filteredSuitesResults;
    }
}
