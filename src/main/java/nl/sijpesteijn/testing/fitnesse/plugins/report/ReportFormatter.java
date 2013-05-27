package nl.sijpesteijn.testing.fitnesse.plugins.report;

/**
 * Report formatter interface.
 */
public interface ReportFormatter {

    /**
     * Extract the html from the mafia test result.
     *
     * @param testResult - a MafiaTestResult.
     * @param testType   - the test type TEST/SUITE/FILTERED_SUITE
     * @return the html
     */
    String getHtml(MafiaTestResult testResult, TestType testType);
}
