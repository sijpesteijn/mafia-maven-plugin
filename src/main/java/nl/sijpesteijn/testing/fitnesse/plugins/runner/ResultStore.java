package nl.sijpesteijn.testing.fitnesse.plugins.runner;

import fitnesse.wiki.PageType;
import nl.sijpesteijn.testing.fitnesse.plugins.report.MafiaTestSummary;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaException;

import java.io.File;

/**
 * Result store interface.
 */
public interface ResultStore {

    /**
     * Save the test details and return a MafiaTestSummary.
     *
     * @param content          - the wiki html content.
     * @param resultsDirectory - location of the test results.
     * @param testTime         - time used for this test.
     * @param pageType         - type of test page.
     * @param wikiPage         - the name of the test.  @return {@link nl.sijpesteijn.testing.fitnesse.plugins.report.MafiaTestSummary}
     * @param suiteFilter      - Suite filter.
     * @throws MafiaException in case an error.
     */
    MafiaTestSummary saveResult(String content, File resultsDirectory, Long testTime, PageType pageType, String wikiPage, String suiteFilter) throws MafiaException;

    /**
     * Save the testsummary to disk.
     *
     * @param testSummary      - summary to save.
     * @param resultsDirectory - directory to save to.
     * @throws MafiaException
     */
    void saveSummary(MafiaTestSummary testSummary, File resultsDirectory) throws MafiaException;

    /**
     * Get the testsummary from disk.
     *
     * @param resultsDirecotry - directory to find the summary.
     * @return
     * @throws MafiaException
     */
    MafiaTestSummary getSummary(File resultsDirecotry) throws MafiaException;
}
