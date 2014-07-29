package nl.sijpesteijn.testing.fitnesse.plugins.report;

import fitnesse.testsystems.TestSummary;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * MafiaTestSummary.
 */
public class MafiaTestSummary extends TestSummary {

    /**
     * Date format.
     */
    private DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * Time stamp.
     */
    private String timeStamp = formatter.format(new Date());

    /**
     * Test duration.
     */
    private long testTime;

    /**
     * Test date.
     */
    private String runDate;

    public MafiaTestSummary(final int right, final int wrong, final int ignores, final int exceptions) {
        super(right, wrong, ignores, exceptions);
    }

    public MafiaTestSummary() {

    }

    /**
     * Set the report time stamp to now.
     */
    public final void updateReportTimeStamp() {
        timeStamp = formatter.format(new Date());
    }

    /**
     * Return the timestamp.
     *
     * @return {@link java.lang.String}
     */
    public final String getTimeStamp() {
        return timeStamp;
    }

    /**
     * Get the report file name.
     *
     * @return {@link java.lang.String}
     */
    public final String getReportFileName() {
        return getTimeStamp() + "_" + getRight() + "_" + getWrong() + "_" + getIgnores() + "_" + getExceptions()
                + ".xml";
    }

    /**
     * Set the test duration.
     *
     * @param testTime long
     */
    public final void setTestTime(final long testTime) {
        this.testTime = testTime;
    }

    /**
     * Get the test time.
     *
     * @return long
     */
    public final long getTestTime() {
        return testTime;
    }

    /**
     * @return {@link java.lang.String}
     */
    @Override
    public final String toString() {
        return super.toString() + ", file: " + getReportFileName();
    }

    /**
     * Get the run date.
     *
     * @return - date.
     */
    public final String getRunDate() {
        return runDate;
    }

    /**
     * Set the run date.
     *
     * @param runDate - date.
     */
    public final void setRunDate(final String runDate) {
        this.runDate = runDate;
    }
}
