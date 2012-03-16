package nl.sijpesteijn.testing.fitnesse.plugins.responders;

import java.io.File;

import fitnesse.responders.testHistory.PageHistory;
import fitnesse.responders.testHistory.TestHistory;
import fitnesse.responders.testHistory.TestResultRecord;

public class MafiaReportResponder {
    private File resultsDirectory = new File("/Users/gijs/git/denieuwetop2000/test/FitNesseRoot/files/testResults/");
    private TestHistory history;
    private PageHistory pageHistory;

    public TestResultRecord makeResponse() {
        history = new TestHistory();
        final String pageName = "FrontPage.RegisterTest";
        history.readPageHistoryDirectory(resultsDirectory, pageName);
        pageHistory = history.getPageHistory(pageName);

        return pageHistory.get(pageHistory.getLatestDate());
    }

    public void setResultsDirectory(final File resultsDirectory) {
        this.resultsDirectory = resultsDirectory;
    }

    public static void main(final String[] args) {
        final MafiaReportResponder responder = new MafiaReportResponder();
        responder.makeResponse();
    }
}
