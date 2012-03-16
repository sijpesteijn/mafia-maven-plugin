package nl.sijpesteijn.testing.fitnesse.plugins.managers;

import java.util.List;

import nl.sijpesteijn.testing.fitnesse.plugins.executioners.FitNesseTestExecutioner;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.RunnerPluginConfig;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Plugin manager responsible for running FitNesse tests.
 * 
 */
public class RunnerPluginManager implements PluginManager {

    private final RunnerPluginConfig runnerPluginConfig;
    private final FitNesseTestExecutioner testExecutioner;

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
        runTests(runnerPluginConfig.getTests());
        runSuites(runnerPluginConfig.getSuites());
        runBySuiteFilter(runnerPluginConfig.getSuiteFilter(), runnerPluginConfig.getSuitePageName());
    }

    /**
     * Create the FitNesse test executioner.
     * 
     * @return {@link nl.sijpesteijn.testing.fitnesse.plugins.executioners.FitNesseTestExecutioner}
     * @throws MojoExecutionException
     */
    private FitNesseTestExecutioner createTestExecutioner() throws MojoExecutionException {
        try {
            return new FitNesseTestExecutioner(runnerPluginConfig.getWikiRoot(), runnerPluginConfig.getNameRootPage(),
                    runnerPluginConfig.getFitnesseOutputDirectory(), runnerPluginConfig.getPort());
        } catch (final Exception e) {
            throw new MojoExecutionException(e.getMessage());
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
                testExecutioner.runTest(testName);
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
                testExecutioner.runTestSuite(suiteName);
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
            testExecutioner.runByTagFilter(suiteFilter, suitePageName);
        }
    }
}
