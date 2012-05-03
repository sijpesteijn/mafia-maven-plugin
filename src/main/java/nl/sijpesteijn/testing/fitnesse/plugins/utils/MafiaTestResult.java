package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fitnesse.responders.run.TestSummary;
import fitnesse.wiki.PageType;

public class MafiaTestResult {

    private final String pageName;
    private final TestSummary testSummary;
    private String htmlResult;
    private final PageType pageType;
    private final boolean addToOverview;
    private List<MafiaTestResult> subTestResults;

    public MafiaTestResult(final PageType pageType, final String pageName, final TestSummary testSummary,
            final String htmlResult, final boolean addToOverview) {
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

    public void setSubResults(final List<MafiaTestResult> subTestResults) {
        this.subTestResults = subTestResults;
    }

    public Collection<? extends MafiaTestResult> getSubTestResults() {
        return subTestResults;
    }

    public void addSubResult(final MafiaTestResult mafiaTestResult) {
        if (subTestResults == null) {
            subTestResults = new ArrayList<MafiaTestResult>();
        }
        subTestResults.add(mafiaTestResult);
    }

    public void setHtmlResult(final String updatedSubHtml) {
        htmlResult = updatedSubHtml;
    }
}
