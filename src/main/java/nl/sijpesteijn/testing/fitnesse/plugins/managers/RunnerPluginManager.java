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

/**
 * Plugin manager responsible for running FitNesse tests.
 * 
 */
public class RunnerPluginManager implements PluginManager {

	private final RunnerPluginConfig runnerPluginConfig;
	private final FitNesseTestExecutioner testExecutioner;
	private final TestSummaryAndDuration summary = new TestSummaryAndDuration();

	/**
	 * 
	 * @param runnerPluginConfig
	 *            {@link nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.RunnerPluginConfig}
	 * @throws MojoExecutionException
	 */
	public RunnerPluginManager(final RunnerPluginConfig runnerPluginConfig) throws MojoExecutionException {
		this.runnerPluginConfig = runnerPluginConfig;
		testExecutioner = createTestExecutioner();
	}

	/**
	 * Run the tests.
	 */
	@Override
	public void run() throws MojoFailureException, MojoExecutionException {
		this.runTests(runnerPluginConfig.getTests());
		this.runSuites(runnerPluginConfig.getSuites());
		this.runBySuiteFilter(runnerPluginConfig.getSuiteFilter(), runnerPluginConfig.getSuitePageName());
		final ResultWriter resultWriter = new ResultWriterImpl(runnerPluginConfig.getFitnesseOutputDirectory());
		resultWriter.write(getSummary());

	}

	/**
	 * Create the FitNesse test executioner.
	 * 
	 * @return {@link nl.sijpesteijn.testing.fitnesse.plugins.executioners.FitNesseTestExecutioner}
	 * @throws MojoExecutionException
	 */
	private FitNesseTestExecutioner createTestExecutioner() throws MojoExecutionException {
		FitNesseTestExecutioner executioner;
		if (runnerPluginConfig.isResultsListenerClassSet()) {
			final ResultsListener resultsListener = loadResultsListener();
			executioner = new FitNesseTestExecutioner(runnerPluginConfig.getWikiRoot(),
					runnerPluginConfig.getNameRootPage(), runnerPluginConfig.getFitnesseOutputDirectory(),
					runnerPluginConfig.getPort(), resultsListener);
		} else {
			executioner = new FitNesseTestExecutioner(runnerPluginConfig.getWikiRoot(),
					runnerPluginConfig.getNameRootPage(), runnerPluginConfig.getFitnesseOutputDirectory(),
					runnerPluginConfig.getPort());
		}
		return executioner;
	}

	/**
	 * Load the results listener.
	 * 
	 * @return {@link fitnesse.responders.run.ResultsListener}
	 * @throws MojoExecutionException
	 */
	private ResultsListener loadResultsListener() throws MojoExecutionException {
		try {
			return (ResultsListener) this.getClass().getClassLoader()
					.loadClass(runnerPluginConfig.getResultsListenerClass()).newInstance();
		} catch (final Throwable e) {
			throw new MojoExecutionException("Could not load resultsListener. ", e);
		}
	}

	/**
	 * Run the tests.
	 * 
	 * @param tests
	 *            {@link java.util.List}
	 * @throws MojoFailureException
	 * @throws MojoExecutionException
	 */
	public void runTests(final List<String> tests) throws MojoFailureException, MojoExecutionException {
		if (tests != null) {
			for (final String testName : tests) {
				final TestSummary testSummary = testExecutioner.runTest(testName);
				updateTestRunStatus(testSummary, testName);
			}
		}

	}

	/**
	 * Run the suites
	 * 
	 * @param suites
	 *            {@link java.util.List}
	 * @throws MojoFailureException
	 * @throws MojoExecutionException
	 */
	public void runSuites(final List<String> suites) throws MojoFailureException, MojoExecutionException {
		if (suites != null) {
			for (final String suiteName : suites) {
				final TestSummary suiteSummary = testExecutioner.runTestSuite(suiteName);
				updateTestRunStatus(suiteSummary, suiteName);
			}
		}
	}

	/**
	 * Run tests by suite filter.
	 * 
	 * @param suiteFilter
	 *            {@link java.lang.String}
	 * @param suitePageName
	 *            {@link java.lang.String}
	 * @throws MojoFailureException
	 * @throws MojoExecutionException
	 */
	public void runBySuiteFilter(final String suiteFilter, final String suitePageName) throws MojoFailureException,
			MojoExecutionException {
		if (suiteFilter != null || suitePageName != null) {
			if (suiteFilter == null || suitePageName == null) {
				throw new MojoFailureException("SuiteFilter and/or SuitePageName not set.");
			}
			final TestSummary suiteSummary = testExecutioner.runByTagFilter(suiteFilter, suitePageName);
			updateTestRunStatus(suiteSummary, suiteFilter);
		}
	}

	/**
	 * Return the summary report.
	 * 
	 * @return {@link nl.sijpesteijn.testing.fitnesse.plugins.executioners.TestSummaryAndDuration}
	 */
	public TestSummaryAndDuration getSummary() {
		return summary;
	}

	/**
	 * Update the summary report.
	 * 
	 * @param testSummary
	 *            {@link fitnesse.responders.run.TestSummary}
	 * @param testName
	 *            {@link java.lang.String}
	 * @throws MojoExecutionException
	 */
	private void updateTestRunStatus(final TestSummary testSummary, final String testName)
			throws MojoExecutionException {
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
