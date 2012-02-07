package nl.sijpesteijn.testing.fitnesse.plugins.executioners;

import java.io.IOException;

import nl.sijpesteijn.testing.fitnesse.plugins.utils.FitNesseTestResultsListener;

import org.apache.maven.plugin.MojoFailureException;

import fitnesse.Arguments;
import fitnesse.responders.run.JavaFormatter;
import fitnesse.responders.run.ResultsListener;
import fitnesse.responders.run.TestSummary;
import fitnesseMain.FitNesseMain;

public class FitNesseTestExecutioner implements TestExecutioner {
    public static final String PAGE_TYPE_SUITE = "suite";
    public static final String PAGE_TYPE_TEST = "test";
    private final String fitNesseRootPath;
    private final String outputPath;
    private final int fitnessePort;
    private final ResultsListener resultsListener;

    public FitNesseTestExecutioner(final String fitNesseRootPath, final String outputPath, final int fitnessePort,
                                   final ResultsListener resultsListener)
    {
        this.fitNesseRootPath = fitNesseRootPath;
        this.outputPath = outputPath;
        this.fitnessePort = fitnessePort;
        this.resultsListener = resultsListener;
    }

    public FitNesseTestExecutioner(final String fitNesseRootPath, final String outputPath, final int fitnessePort) {
        this(fitNesseRootPath, outputPath, fitnessePort, new FitNesseTestResultsListener(null));
    }

    public TestSummary runTest(final String testName) throws MojoFailureException {
        return run(testName, PAGE_TYPE_TEST, null);
    }

    public TestSummary runTestSuite(final String suiteName) throws MojoFailureException {
        return run(suiteName, PAGE_TYPE_SUITE, null);
    }

    public TestSummary runByTagFilter(final String suiteFilter, final String suitePageName) throws MojoFailureException
    {
        return run(suitePageName, PAGE_TYPE_SUITE, suiteFilter);
    }

    private TestSummary run(final String testName, final String pageType, final String suiteFilter)
            throws MojoFailureException
    {
        final JavaFormatter testFormatter = JavaFormatter.getInstance(testName);
        try {
            final JavaFormatter.FolderResultsRepository mockResultsRepository =
                    new JavaFormatter.FolderResultsRepository(outputPath, fitNesseRootPath);
            testFormatter.setResultsRepository(mockResultsRepository);
        } catch (final IOException ioException) {
            throw new MojoFailureException(ioException.getMessage());
        }
        // maak eigen listener.
        testFormatter.setListener(this.resultsListener);
        final Arguments arguments = new Arguments();
        arguments.setDaysTillVersionsExpire("0");
        arguments.setInstallOnly(false);
        arguments.setOmitUpdates(true);
        arguments.setPort(String.valueOf(fitnessePort));
        arguments.setRootPath(fitNesseRootPath);
        arguments.setCommand(getCommand(testName, pageType, suiteFilter));
        FitNesseMain.dontExitAfterSingleCommand = true;
        try {
            FitNesseMain.launchFitNesse(arguments);
        } catch (final Exception exception) {
            throw new MojoFailureException(exception.getMessage());
        }
        return testFormatter.getTotalSummary();
    }

    private String getCommand(final String pageName, final String pageType, final String suiteFilter) {
        if (suiteFilter != null)
            return pageName + "?responder=suite&suiteFilter=" + suiteFilter;
        else
            return pageName + "?" + pageType + "&nohistory=true&format=java";
    }

    public static void main(final String[] args) throws Exception {
        final FitNesseTestExecutioner testExecutioner =
                new FitNesseTestExecutioner("/home/gijs/development/contact/contact-fitnesse/",
                    "/home/gijs/development/contact/contact-fitnesse/target/", 9091, null);
        testExecutioner.runTestSuite("FrontPage.ContactBusinessSuite");
    }
}
