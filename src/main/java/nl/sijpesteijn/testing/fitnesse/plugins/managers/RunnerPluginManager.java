package nl.sijpesteijn.testing.fitnesse.plugins.managers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.RunnerPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.FitNesseComanderConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.FitNesseCommander;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import fitnesse.responders.run.TestSummary;

/**
 * Plugin manager responsible for running FitNesse tests.
 * 
 */
public class RunnerPluginManager implements PluginManager {

    private final RunnerPluginConfig runnerPluginConfig;
    private final FitNesseCommander fitNesseCommander;
    private final Map<String, TestSummary> testSummaries = new HashMap<String, TestSummary>();

    /**
     * 
     * @param runnerPluginConfig
     *        {@link nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.RunnerPluginConfig}
     * @throws MojoExecutionException
     */
    public RunnerPluginManager(final RunnerPluginConfig runnerPluginConfig) throws MojoExecutionException {
        this.runnerPluginConfig = runnerPluginConfig;
        fitNesseCommander = createFitNesseCommander();
    }

    private FitNesseCommander createFitNesseCommander() {
        final FitNesseComanderConfig fitNesseCommanderConfig = new FitNesseComanderConfig();
        fitNesseCommanderConfig.setLog(runnerPluginConfig.getLog());
        fitNesseCommanderConfig.setFitNessePort(runnerPluginConfig.getPort());
        fitNesseCommanderConfig.setLogDirectory(runnerPluginConfig.getLogDirectory());
        fitNesseCommanderConfig.setNameRootPage(runnerPluginConfig.getNameRootPage());
        fitNesseCommanderConfig.setRetainDays(runnerPluginConfig.getRetainDays());
        fitNesseCommanderConfig.setWikiRoot(runnerPluginConfig.getWikiRoot());
        fitNesseCommanderConfig.setTestResultsDirectoryName(runnerPluginConfig.getFitnesseOutputDirectory());
        return new FitNesseCommander(fitNesseCommanderConfig);
    }

    /**
     * Run the tests.
     */
    @Override
    public void run() throws MojoFailureException, MojoExecutionException {
        fitNesseCommander.clearTestResultsDirectory();
        fitNesseCommander.start();
        runTests(runnerPluginConfig.getTests());
        runSuites(runnerPluginConfig.getSuites());
        runBySuiteFilter(runnerPluginConfig.getSuiteFilter(), runnerPluginConfig.getSuitePageName());
        testSummaryCheck();
        try {
            fitNesseCommander.stop();
        } catch (final Exception e) {
            throw new MojoExecutionException("Could not stop fitnesse", e);
        }
    }

    private void testSummaryCheck() throws MojoExecutionException {
        boolean stop = false;
        for (final String key : testSummaries.keySet()) {
            final TestSummary testSummary = testSummaries.get(key);
            if (testSummary.wrong > 0 && runnerPluginConfig.isStopTestsOnWrong()) {
                stop = true;
                runnerPluginConfig.getLog().debug(key + " result: wrong");
            }
            if (testSummary.ignores > 0 && runnerPluginConfig.isStopTestsOnIgnore()) {
                stop = true;
                runnerPluginConfig.getLog().debug(key + " result: ignore");
            }
            if (testSummary.exceptions > 0 && runnerPluginConfig.isStopTestsOnException()) {
                stop = true;
                runnerPluginConfig.getLog().debug(key + " result: exception");
            }
        }
        if (stop) {
            throw new MojoExecutionException("FitNesse test not executed successfully");
        }
    }

    /**
     * Run the tests.
     * 
     * @param tests
     *        {@link java.util.List}
     * @throws MojoFailureException
     * @throws MojoExecutionException
     */
    public void runTests(final List<String> tests) throws MojoFailureException, MojoExecutionException {
        if (tests != null) {
            for (final String testName : tests) {
                testSummaries.put(testName, fitNesseCommander.runTest(testName));
            }
        }

    }

    /**
     * Run the suites
     * 
     * @param suites
     *        {@link java.util.List}
     * @throws MojoFailureException
     * @throws MojoExecutionException
     */
    public void runSuites(final List<String> suites) throws MojoFailureException, MojoExecutionException {
        if (suites != null) {
            for (final String suiteName : suites) {
                testSummaries.put(suiteName, fitNesseCommander.runTestSuite(suiteName));
            }
        }
    }

    /**
     * Run tests by suite filter.
     * 
     * @param suiteFilter
     *        {@link java.lang.String}
     * @param suitePageName
     *        {@link java.lang.String}
     * @throws MojoFailureException
     * @throws MojoExecutionException
     */
    public void runBySuiteFilter(final String suiteFilter, final String suitePageName) throws MojoFailureException,
            MojoExecutionException
    {
        if (suiteFilter != null || suitePageName != null) {
            if (suiteFilter == null || suitePageName == null) {
                throw new MojoFailureException("SuiteFilter and/or SuitePageName not set.");
            }
            testSummaries.put(suitePageName, fitNesseCommander.runByTagFilter(suiteFilter, suitePageName));
        }
    }
}
