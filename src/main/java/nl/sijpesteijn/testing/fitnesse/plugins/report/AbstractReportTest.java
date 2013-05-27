package nl.sijpesteijn.testing.fitnesse.plugins.report;

/**
 * AbstractReportTest.
 */
public abstract class AbstractReportTest {

    /**
     * Creates a MafiaTestSummary.
     *
     * @param right      - number of successful tests.
     * @param wrong      - number of wrong tests.
     * @param exceptions - number of exceptions thrown.
     * @param ignores    - number of ignored tests.
     * @return {@link nl.sijpesteijn.testing.fitnesse.plugins.report.MafiaTestSummary}
     */
    public final MafiaTestSummary getSummary(final int right, final int wrong, final int exceptions,
                                             final int ignores) {
        MafiaTestSummary testSummary = new MafiaTestSummary();
        testSummary.wrong = wrong;
        testSummary.right = right;
        testSummary.exceptions = exceptions;
        testSummary.ignores = ignores;
        return testSummary;
    }
}
