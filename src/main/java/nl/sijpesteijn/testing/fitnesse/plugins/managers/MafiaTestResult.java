package nl.sijpesteijn.testing.fitnesse.plugins.managers;

import fitnesse.responders.testHistory.TestResultRecord;

public class MafiaTestResult {

    private final String pageName;
    private final TestResultRecord testResultRecord;
    private final String htmlResult;

    public MafiaTestResult(final String pageName, final TestResultRecord testResultRecord, final String htmlResult) {
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
}
