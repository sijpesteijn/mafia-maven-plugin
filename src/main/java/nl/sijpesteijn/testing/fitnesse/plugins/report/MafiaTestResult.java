package nl.sijpesteijn.testing.fitnesse.plugins.report;

import java.io.File;

/**
 * MafiaTestResult.
 */
public class MafiaTestResult {
    /**
     * Mafia Test result summary.
     */
    private final MafiaTestSummary testSummary;

    /**
     * Html result.
     */
    private String htmlResult;

    /**
     * Test file.
     */
    private File testFile;

    /**
     * Constructor.
     *
     * @param testFile    - the test file created by FitNesse.
     * @param testSummary - the test summary.
     * @param htmlResult  - the html result.
     */
    public MafiaTestResult(final File testFile, final MafiaTestSummary testSummary,
                           final String htmlResult) {
        this.testFile = testFile;
        this.testSummary = testSummary;
        this.htmlResult = htmlResult;
    }

    /**
     * @return {@link nl.sijpesteijn.testing.fitnesse.plugins.report.MafiaTestSummary}
     */
    public final MafiaTestSummary getTestSummary() {
        return testSummary;
    }

    /**
     * @return {@link java.lang.String}
     */
    public final String getHtmlResult() {
        return htmlResult;
    }

    /**
     * @return {@link java.lang.String}
     */
    public final String getName() {
        return testFile.getName().replaceAll(".html", "");
    }
}
