package nl.sijpesteijn.testing.fitnesse.plugins.runner;

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
     * @param fileName         - the name of the test.
     * @return {@link nl.sijpesteijn.testing.fitnesse.plugins.report.MafiaTestSummary}
     * @throws MafiaException in case an error.
     */
    MafiaTestSummary saveResult(String content, File resultsDirectory, String fileName) throws MafiaException;
}
