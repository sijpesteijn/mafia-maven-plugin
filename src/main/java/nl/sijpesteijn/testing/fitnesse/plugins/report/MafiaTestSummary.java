package nl.sijpesteijn.testing.fitnesse.plugins.report;

import fitnesse.testsystems.TestSummary;
import fitnesse.wiki.PageType;

import java.io.File;
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
    private long runDate;

    /**
     * Wiki page name
     */
    private String wikiPage;

    /**
     * Page type (suite, test or static)
     */
    private PageType pageType;

    /**
     * Suite filter.
     */
    private String suiteFilter;

    /**
     *
     * @param right - successful tests
     * @param wrong - failed tests
     * @param ignores - tests with ignores
     * @param exceptions - tests with exceptions
     * @param testTime - time it took to complete the test
     * @param runDate - time of test.
     * @param wikiPage - Name of the wiki page.
     * @param pageType - type of test page.
     */
    public MafiaTestSummary(final int right, final int wrong, final int ignores, final int exceptions, long testTime, long runDate, String wikiPage, PageType pageType) {
        this(right, wrong, ignores, exceptions);
        this.testTime = testTime;
        this.runDate = runDate;
        this.wikiPage = wikiPage;
        this.pageType = pageType;
    }

    /**
     *
     *  @param right - successful tests
     * @param wrong - failed tests
     * @param ignores - tests with ignores
     * @param exceptions - tests with exceptions
     */
    public MafiaTestSummary(final int right, final int wrong, final int ignores, final int exceptions) {
        super(right, wrong, ignores, exceptions);
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
        return getPageType().toString().toLowerCase().concat("s") + File.separator + getWikiPage()+ File.separator + "fitnesse-results.xml";
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
        return super.toString() + "," + " file: " + getReportFileName();
    }

    /**
     * Get the run date.
     *
     * @return - date.
     */
    public final long getRunDate() {
        return runDate;
    }

    /**
     * Set the run date.
     *
     * @param runDate - date.
     */
    public final void setRunDate(final long runDate) {
        this.runDate = runDate;
    }

    public String getWikiPage() {
        return wikiPage;
    }

    public void setWikiPage(String wikiPage) {
        this.wikiPage = wikiPage;
    }

    public void setPageType(PageType pageType) {
        this.pageType = pageType;
    }

    public PageType getPageType() {
        return pageType;
    }

    public boolean successful() {
        return this.getExceptions() == 0 && this.getWrong() == 0;
    }

    public void setSuiteFilter(String suiteFilter) {
        this.suiteFilter = suiteFilter;
    }

    public String getSuiteFilter() {
        return suiteFilter;
    }
}
