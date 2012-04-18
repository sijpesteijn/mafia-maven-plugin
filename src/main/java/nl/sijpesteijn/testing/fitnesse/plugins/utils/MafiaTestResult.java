package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import fitnesse.responders.run.TestSummary;
import fitnesse.wiki.PageType;

public class MafiaTestResult {

    private final String pageName;
    private final TestSummary testSummary;
    private final String htmlResult;
    private final PageType pageType;
    private final boolean addToOverview;

    public MafiaTestResult(final PageType pageType, final String pageName, final TestSummary testSummary,
                           final String htmlResult, final boolean addToOverview)
    {
        this.pageType = pageType;
        this.pageName = pageName;
        this.testSummary = testSummary;
        this.htmlResult = htmlResult;
        this.addToOverview = addToOverview;
    }

    public String getPageName() {
        return pageName;
    }

    public TestSummary getTestSummary() {
        return testSummary;
    }

    public String getHtmlResult() {
        return htmlResult;
    }

    public PageType getPageType() {
        return pageType;
    }

    public boolean isAddToOverview() {
        return addToOverview;
    }
}
