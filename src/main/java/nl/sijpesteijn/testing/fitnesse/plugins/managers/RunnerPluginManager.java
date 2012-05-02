package nl.sijpesteijn.testing.fitnesse.plugins.managers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.RunnerPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.FitNesseComanderConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.FitNesseCommander;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.FitNesseExtractor;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import fitnesse.responders.run.TestSummary;

/**
 * Plugin manager responsible for running FitNesse tests.
 * 
 */
public class RunnerPluginManager implements PluginManager {

    private final RunnerPluginConfig runnerPluginConfig;
    private final FitNesseCommander fitNesseCommander;
    private final Map<String, TestSummary> testSummaries = new HashMap<String, TestSummary>();
    private final Log mavenLogger;

    /**
     * 
     * @param runnerPluginConfig
     *        {@link nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.RunnerPluginConfig}
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public RunnerPluginManager(final RunnerPluginConfig runnerPluginConfig) throws MojoExecutionException,
            MojoFailureException
    {
        this.runnerPluginConfig = runnerPluginConfig;
        mavenLogger = runnerPluginConfig.getMavenLogger();
        fitNesseCommander = createFitNesseCommander();
    }

    private FitNesseCommander createFitNesseCommander() throws MojoFailureException {
        final FitNesseComanderConfig fitNesseCommanderConfig = new FitNesseComanderConfig();
        fitNesseCommanderConfig.setMavenLogger(runnerPluginConfig.getMavenLogger());
        fitNesseCommanderConfig.setFitNessePort(runnerPluginConfig.getPort());
        fitNesseCommanderConfig.setLogDirectory(runnerPluginConfig.getLogDirectory());
        fitNesseCommanderConfig.setNameRootPage(runnerPluginConfig.getNameRootPage());
        fitNesseCommanderConfig.setRetainDays(runnerPluginConfig.getRetainDays());
        fitNesseCommanderConfig.setWikiRoot(runnerPluginConfig.getWikiRoot());
        fitNesseCommanderConfig.setTestResultsDirectoryName(runnerPluginConfig.getMafiaTestResultsDirectory());
        return new FitNesseCommander(fitNesseCommanderConfig);
    }

    /**
     * Run the tests.
     */
    @Override
    public void run() throws MojoFailureException, MojoExecutionException {
        FitNesseExtractor.extract(runnerPluginConfig.getWikiRoot(), runnerPluginConfig.getRepositoryDirectory());
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

    private void testSummaryCheck() throws MojoExecutionException, MojoFailureException {
        boolean stop = false;
        for (final String key : testSummaries.keySet()) {
            final TestSummary testSummary = testSummaries.get(key);
            if (testSummary.wrong > 0 && runnerPluginConfig.isStopTestsOnWrong()) {
                stop = true;
                runnerPluginConfig.getMavenLogger().info(key + " result: failed");
            }
            if (testSummary.ignores > 0 && runnerPluginConfig.isStopTestsOnIgnore()) {
                stop = true;
                runnerPluginConfig.getMavenLogger().info(key + " result: ignore");
            }
            if (testSummary.exceptions > 0 && runnerPluginConfig.isStopTestsOnException()) {
                stop = true;
                runnerPluginConfig.getMavenLogger().info(key + " result: exception");
            }
        }
        if (stop) {
            throw new MojoFailureException("One or more tests or suites returned unexpected results.");
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
        if (tests != null && !tests.isEmpty()) {
            mavenLogger.info("Running tests...");
            for (final String testName : tests) {
                final TestSummary summary = fitNesseCommander.runTest(testName);
                mavenLogger.info("Test: " + testName + " (" + summary.toString() + ")");
                testSummaries.put(testName, summary);

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
        if (suites != null && !suites.isEmpty()) {
            mavenLogger.info("Running suites...");
            for (final String suiteName : suites) {
                final TestSummary testSummary = fitNesseCommander.runTestSuite(suiteName);
                mavenLogger.info("Suite: " + suiteName + " (" + testSummary.toString() + ")");
                testSummaries.put(suiteName, testSummary);
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
            mavenLogger.info("Running suite with filter (suiteFilter=" + suiteFilter + ", suitePageName="
                    + suitePageName + ") ....");
            if (suiteFilter == null || suitePageName == null) {
                throw new MojoFailureException("SuiteFilter and/or SuitePageName not set.");
            }
            testSummaries.put(suitePageName, fitNesseCommander.runByTagFilter(suiteFilter, suitePageName));
        }
    }
}
