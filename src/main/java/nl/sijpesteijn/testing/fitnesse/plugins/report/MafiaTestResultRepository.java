package nl.sijpesteijn.testing.fitnesse.plugins.report;

import nl.sijpesteijn.testing.fitnesse.plugins.runner.ResultStore;
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

    private ResultStore resultStore;

    /**
     * Constructor.
     *
     * @param outputDirectory {@link java.io.File}
     * @throws MafiaException thrown in case of an error.
     */
    public MafiaTestResultRepository(final File outputDirectory, final ResultStore resultStore) throws MafiaException {
        this.resultStore = resultStore;
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
        List<MafiaTestResult> testDetails = new ArrayList<>();
        File testDir = new File(outputDirectory, sub);
        if (testDir != null && testDir.isDirectory()) {
            File[] tests = testDir.listFiles();
            for (File test : tests) {
                try {
                    File wikiPage = new File(test, "wikipage.html");
                    String html = FileUtils.readFileToString(wikiPage);
                    File sum = new File(test, "mafiaresults.properties");
                    MafiaTestSummary summary = resultStore.getSummary(sum);
                    MafiaTestResult testResult = new MafiaTestResult(test, summary, html);
                    testDetails.add(testResult);
                } catch (IOException e) {
                    throw new MafiaException("Could not make mafia test result.", e);
                }
            }
        }
        return testDetails;
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
