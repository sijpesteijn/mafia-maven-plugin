package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import fitnesse.responders.testHistory.TestResultRecord;
import fitnesse.wiki.PageType;

public class MafiaTestResult {

    private final String pageName;
    private final TestResultRecord testResultRecord;
    private final String htmlResult;
    private final PageType pageType;

    public MafiaTestResult(final PageType pageType, final String pageName, final TestResultRecord testResultRecord,
                           final String htmlResult)
    {
        this.pageType = pageType;
        this.pageName = pageName;
        this.testResultRecord = testResultRecord;
        this.htmlResult = htmlResult;
    }

    public String getPageName() {
        return pageName;
    }

    public TestResultRecord getTestResultRecord() {
        return testResultRecord;
    }

    public String getHtmlResult() {
        return htmlResult;
    }

    public PageType getPageType() {
        return pageType;
    }
}
