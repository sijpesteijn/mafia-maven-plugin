package nl.sijpesteijn.testing.fitnesse.plugins;

import java.util.Arrays;

import nl.sijpesteijn.testing.fitnesse.plugins.managers.PluginManager;
import nl.sijpesteijn.testing.fitnesse.plugins.managers.PluginManagerFactory;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.PluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.RunnerPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.RunnerPluginConfig.Builder;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Goal to run the Fitnesse tests.
 * 
 * @goal run-tests
 * 
 * @phase integration-test
 */
public class FitnesseRunnerMojo extends AbstractMojo {

    /**
     * @parameter expression="${run-tests.port}" default-value="9091"
     */
    private int port;

    /**
     * Location of the file.
     * 
     * @parameter expression="${run-tests.wikiRoot}" default-value="${basedir}"
     * @required
     */
    private String wikiRoot;

    /**
     * The directory where the Fitnesse reports have been generated.
     * 
     * @parameter expression="${run-tests.fitnesseReportDirectory}" default-value="${project.build.directory}/fitnesse/"
     */
    private String fitnesseOutputDirectory;

    /**
     * 
     * @parameter expression="${run-tests.tests}"
     */
    private String[] tests;

    /**
     * 
     * @parameter expression="${run-tests.suites}"
     */
    private String[] suites;

    /**
     * @parameter expression="${run-tests.suitePageName}"
     */
    private String suitePageName;

    /**
     * 
     * @parameter expression="${run-tests.suiteFilter}"
     */
    private String suiteFilter;

    /**
     * 
     * @parameter expression="${run-tests.stopTestsOnFailure}" default-value="true"
     * 
     */
    private boolean stopTestsOnFailure;

    /**
     * 
     * @parameter expression="${run-tests.stopTestsOnIgnore}" default-value="false"
     * 
     */
    private boolean stopTestsOnIgnore;

    /**
     * 
     * @parameter expression="${run-tests.stopTestsOnException}" default-value="true"
     * 
     */
    private boolean stopTestsOnException;

    /**
     * 
     * @parameter expression="${run-tests.stopTestsOnWrong}" default-value="true"
     * 
     */
    private boolean stopTestsOnWrong;

    /**
     * 
     * @parameter expression="${run-tests.resultsListener}" default-value=""
     * 
     */
    private String resultsListenerClass;

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final PluginConfig runnerPluginConfig = getPluginConfig();
        getLog().info("Runner config: " + runnerPluginConfig.toString());
        final PluginManager pluginManager = new PluginManagerFactory().getPluginManager(runnerPluginConfig);
        try {
            pluginManager.run();
        } catch (final Exception e) {
            throw new MojoFailureException("" + e);
        }
    }

    private RunnerPluginConfig getPluginConfig() {
        final Builder pluginConfigBuilder = new RunnerPluginConfig.Builder();
        pluginConfigBuilder.setResultsListenerClass(resultsListenerClass);
        pluginConfigBuilder.setWikiRoot(this.wikiRoot);
        pluginConfigBuilder.setFitNesseOutputDirectory(this.fitnesseOutputDirectory);
        pluginConfigBuilder.setPort(this.port);
        pluginConfigBuilder.setStopTestsOnException(stopTestsOnException);
        pluginConfigBuilder.setStopTestsOnFailure(stopTestsOnFailure);
        pluginConfigBuilder.setStopTestsOnIgnore(stopTestsOnIgnore);
        pluginConfigBuilder.setStopTestsOnWrong(stopTestsOnWrong);
        pluginConfigBuilder.setTests(Arrays.asList(tests));
        pluginConfigBuilder.setSuites(Arrays.asList(suites));
        pluginConfigBuilder.setSuiteFilter(suiteFilter);
        pluginConfigBuilder.setSuitePageName(suitePageName);
        return pluginConfigBuilder.build();
    }

}
