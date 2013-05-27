package nl.sijpesteijn.testing.fitnesse.plugins.runner;

import fitnesse.wiki.PageType;
import nl.sijpesteijn.testing.fitnesse.plugins.report.MafiaTestSummary;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaException;

/**
 * TestCaller interface.
 */
public interface TestCaller {

    /**
     * Call the url.
     *
     * @param wikiPage - the wikiPage.
     * @param pageType - the Page type.
     * @param suiteFilter - optional suite filter.
     * @param subDirectory - sub directory.
     * @return - mafia test summary.
     * @throws MafiaException thrown when an error occurs.
     */
    MafiaTestSummary test(final String wikiPage, final PageType pageType, final String suiteFilter,
                          final String subDirectory) throws MafiaException;
}
