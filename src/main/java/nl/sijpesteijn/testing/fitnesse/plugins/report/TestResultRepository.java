package nl.sijpesteijn.testing.fitnesse.plugins.report;

import java.util.List;

/**
 * Test result repository interface.
 */
public interface TestResultRepository {

    /**
     * Get the list of test results.
     *
     * @return {@link java.util.List} of {@link nl.sijpesteijn.testing.fitnesse.plugins.report.MafiaTestResult}'s.
     */
    List<MafiaTestResult> getTestResults();

    /**
     * Get the list of suite results.
     *
     * @return {@link java.util.List} of {@link nl.sijpesteijn.testing.fitnesse.plugins.report.MafiaTestResult}'s.
     */
    List<MafiaTestResult> getSuitesResults();

    /**
     * Get the list of filtered suite results.
     *
     * @return {@link java.util.List} of {@link nl.sijpesteijn.testing.fitnesse.plugins.report.MafiaTestResult}'s.
     */
    List<MafiaTestResult> getFilteredSuitesResults();
}
