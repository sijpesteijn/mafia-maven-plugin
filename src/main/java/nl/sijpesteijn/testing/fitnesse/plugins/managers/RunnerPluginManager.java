package nl.sijpesteijn.testing.fitnesse.plugins.managers;

import java.util.List;

import nl.sijpesteijn.testing.fitnesse.plugins.executioners.FitNesseTestExecutioner;
import nl.sijpesteijn.testing.fitnesse.plugins.executioners.TestSummaryAndDuration;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.RunnerPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.ResultWriter;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.ResultWriterImpl;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import fitnesse.responders.run.ResultsListener;
import fitnesse.responders.run.TestSummary;

public class RunnerPluginManager implements PluginManager {

    private final RunnerPluginConfig runnerPluginConfig;
    private final FitNesseTestExecutioner testExecutioner;
    private final TestSummaryAndDuration summary = new TestSummaryAndDuration();

    public RunnerPluginManager(final RunnerPluginConfig runnerPluginConfig) throws MojoExecutionException {
        this.runnerPluginConfig = runnerPluginConfig;
        testExecutioner = createTestExecutioner();
    }

    @Override
    public void run() throws MojoFailureException, MojoExecutionException {
        this.runTests(runnerPluginConfig.getTests());
        this.runSuites(runnerPluginConfig.getSuites());
        this.runBySuiteFilter(runnerPluginConfig.getSuiteFilter(), runnerPluginConfig.getSuitePageName());
        final ResultWriter resultWriter = new ResultWriterImpl(runnerPluginConfig.getFitnesseOutputDirectory());
        resultWriter.write(getSummary());

    }

    private FitNesseTestExecutioner createTestExecutioner() throws MojoExecutionException {
        FitNesseTestExecutioner executioner;
        if (runnerPluginConfig.isResultsListenerClassSet()) {
            final ResultsListener resultsListener = (ResultsListener) loadResultsListener();
            executioner =
                    new FitNesseTestExecutioner(runnerPluginConfig.getWikiRoot(),
                        runnerPluginConfig.getFitnesseOutputDirectory(), runnerPluginConfig.getPort(), resultsListener);
        } else {
            executioner =
                    new FitNesseTestExecutioner(runnerPluginConfig.getWikiRoot(),
                        runnerPluginConfig.getFitnesseOutputDirectory(), runnerPluginConfig.getPort());
        }
        return executioner;
    }

    private Object loadResultsListener() throws MojoExecutionException {
        try {
            return this.getClass().getClassLoader().loadClass(runnerPluginConfig.getResultsListenerClass())
                .newInstance();
        } catch (final Throwable e) {
            throw new MojoExecutionException("Could not load resultsListener. ", e);
        }
    }

    public void runTests(final List<String> tests) throws MojoFailureException, MojoExecutionException {
        if (tests != null) {
            for (final String testName : tests) {
                final TestSummary testSummary = testExecutioner.runTest(testName);
                updateTestRunStatus(testSummary, testName);
            }
        }

    }

    public void runSuites(final List<String> suites) throws MojoFailureException, MojoExecutionException {
        if (suites != null) {
            for (final String suiteName : suites) {
                final TestSummary suiteSummary = testExecutioner.runTestSuite(suiteName);
                updateTestRunStatus(suiteSummary, suiteName);
            }
        }
    }

    public void runBySuiteFilter(final String suiteFilter, final String suitePageName) throws MojoFailureException,
            MojoExecutionException
    {
        if (suiteFilter != null || suitePageName != null) {
            if (suiteFilter == null || suitePageName == null) {
                throw new MojoFailureException("SuiteFilter and/or SuitePageName not set.");
            }
            final TestSummary suiteSummary = testExecutioner.runByTagFilter(suiteFilter, suitePageName);
            updateTestRunStatus(suiteSummary, suiteFilter);
        }
    }

    public TestSummaryAndDuration getSummary() {
        return summary;
    }

    private void updateTestRunStatus(final TestSummary testSummary, final String testName)
            throws MojoExecutionException
    {
        summary.wrong = summary.getWrong() + testSummary.getWrong();
        summary.ignores = summary.getIgnores() + testSummary.getIgnores();
        summary.exceptions = summary.getExceptions() + testSummary.getExceptions();
        summary.right = summary.getRight() + testSummary.getRight();
        if (runnerPluginConfig.isStopTestsOnFailure() && summary.getWrong() > 0) {
            throw new MojoExecutionException("Failure: " + testName);
        }
        if (runnerPluginConfig.isStopTestsOnIgnore() && summary.getIgnores() > 0) {
            throw new MojoExecutionException("Ignore: " + testName);
        }
        if (runnerPluginConfig.isStopTestsOnException() && summary.getExceptions() > 0) {
            throw new MojoExecutionException("Exception: " + testName);
        }
        if (runnerPluginConfig.isStopTestsOnWrong() && (summary.getWrong() > 0 || summary.getExceptions() > 0)) {
            throw new MojoExecutionException("Wrong: " + testName);
        }
    }

}
