package nl.sijpesteijn.testing.fitnesse.plugins.runner;

import fitnesse.responders.run.TestSummary;
import fitnesse.wiki.PageType;
import nl.sijpesteijn.testing.fitnesse.plugins.report.MafiaTestSummary;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaException;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.logging.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FitNesseTestRunner.
 */
public class FitNesseTestRunner {

    /**
     * Stop when ignores.
     */
    private final boolean stopTestsOnIgnore;

    /**
     * Stop when exceptions.
     */
    private final boolean stopTestsOnException;

    /**
     * Stop when wrong.
     */
    private final boolean stopTestsOnWrong;

    /**
     * Log.
     */
    private final Log log;

    /**
     * Mafia test summaries.
     */
    private final Map<String, MafiaTestSummary> testSummaries = new HashMap<String, MafiaTestSummary>();

    /**
     * The test caller.
     */
    private TestCaller testCaller;

    /**
     * Constructor.
     *
     * @param testCaller           - The test caller.
     * @param stopTestsOnIgnore    - stop tests when tests are ignored.
     * @param stopTestsOnException - stop tests when an exception occurred.
     * @param stopTestsOnWrong     - stop tests when an error occurred.
     * @param log                  - the logger.
     * @throws MafiaException thrown when an error occurs.
     */
    public FitNesseTestRunner(final TestCaller testCaller,
                              final boolean stopTestsOnIgnore, final boolean stopTestsOnException,
                              final boolean stopTestsOnWrong, final Log log)
            throws MafiaException {
        this.testCaller = testCaller;
        this.stopTestsOnIgnore = stopTestsOnIgnore;
        this.stopTestsOnException = stopTestsOnException;
        this.stopTestsOnWrong = stopTestsOnWrong;
        this.log = log;
    }

    /**
     * Run the tests.
     *
     * @param tests {@link java.util.List} of {@link java.lang.String}'s.
     * @throws MafiaException thrown in cause of an error.
     */
    public final void runTests(final List<String> tests) throws MafiaException {
        if (tests != null && !tests.isEmpty()) {
            log.info("Running tests..");
            for (String test : tests) {
                log.info("Running test: " + test);
                MafiaTestSummary summary = testCaller.test(test, PageType.TEST, null, "/tests/");
                testSummaries.put(test, summary);
                checkStopConditions();
            }
        }
    }

    /**
     * Run the suites.
     *
     * @param suites {@link java.util.List} of {@link java.lang.String}'s.
     * @throws MafiaException thrown in cause of an error.
     */
    public final void runSuites(final List<String> suites) throws MafiaException {
        if (suites != null && !suites.isEmpty()) {
            log.info("Running suites..");
            for (String suite : suites) {
                log.info("Running suite: " + suite);
                MafiaTestSummary summary = testCaller.test(suite, PageType.SUITE, null, "/suites/");
                testSummaries.put(suite, summary);
                summary.updateReportTimeStamp();
                checkStopConditions();
            }
        }

    }

    /**
     * Run the filtered suites.
     *
     * @param suitePageName - the name of the suite (wiki path)
     * @param suiteFilter   - the filter to use.
     * @throws MafiaException thrown in cause of an error.
     */
    public final void runFilteredSuite(final String suitePageName, final String suiteFilter) throws MafiaException {
        if (!StringUtils.isEmpty(suitePageName) && !StringUtils.isEmpty(suiteFilter)) {
            log.info("Running tests by suite filter..");
            log.debug("SuitePageName: " + suitePageName + ", SuiteFilter: " + suiteFilter);
            MafiaTestSummary summary = testCaller.test(suitePageName, PageType.SUITE, suiteFilter, "/filteredSuite/");
            testSummaries.put(suitePageName + " (filter: " + suiteFilter + ")", summary);
            checkStopConditions();
        }
    }

    /**
     * Check the stop conditions.
     *
     * @throws MafiaException - stop condition occurred.
     */
    private void checkStopConditions() throws MafiaException {
        for (final String key : testSummaries.keySet()) {
            final TestSummary testSummary = testSummaries.get(key);
            if (testSummary.wrong > 0 && stopTestsOnWrong) {
                throw new MafiaException(key + " failed with wrong exception.");
            }
            if (testSummary.ignores > 0 && stopTestsOnIgnore) {
                throw new MafiaException(key + " failed with ignore exception.");
            }
            if (testSummary.exceptions > 0 && stopTestsOnException) {
                throw new MafiaException(key + " failed with an exception.");
            }
        }
    }

    /**
     * Get the test summaries.
     *
     * @return {@link java.util.Map} of {@link java.lang.String}'s
     *         and {@link nl.sijpesteijn.testing.fitnesse.plugins.report.MafiaTestSummary}'s.
     */
    public final Map<String, MafiaTestSummary> getTestSummaries() {
        return testSummaries;
    }
}
