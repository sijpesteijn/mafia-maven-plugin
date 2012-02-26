package nl.sijpesteijn.testing.fitnesse.plugins;

import java.util.ArrayList;
import java.util.List;

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
	 * The port number for FitNesse to run the tests.
	 * 
	 * @parameter expression="${run-tests.port}" default-value="9091"
	 */
	private int port;

	/**
	 * Location of the wiki root directory.
	 * 
	 * @parameter expression="${run-tests.wikiRoot}" default-value="${basedir}"
	 * @required
	 */
	private String wikiRoot;

	/**
	 * The directory where the Fitnesse reports have been generated.
	 * 
	 * @parameter expression="${run-tests.fitnesseReportDirectory}"
	 *            default-value="${project.build.directory}/fitnesse/"
	 */
	private String fitnesseOutputDirectory;

	/**
	 * List of test to be run.
	 * 
	 * @parameter expression="${run-tests.tests}"
	 */
	private String[] tests;

	/**
	 * List of suites to be run.
	 * 
	 * @parameter expression="${run-tests.suites}"
	 */
	private String[] suites;

	/**
	 * Name of the suite page name.
	 * 
	 * @parameter expression="${run-tests.suitePageName}"
	 */
	private String suitePageName;

	/**
	 * Suite filter to run in the specified suite (=suitePageName).
	 * 
	 * @parameter expression="${run-tests.suiteFilter}"
	 */
	private String suiteFilter;

	/**
	 * If true, the mojo will stop when it encountered a failure error message.
	 * 
	 * @parameter expression="${run-tests.stopTestsOnFailure}"
	 *            default-value="true"
	 * 
	 */
	private boolean stopTestsOnFailure;

	/**
	 * If true, the mojo will stop when it encountered an ignored error message.
	 * 
	 * @parameter expression="${run-tests.stopTestsOnIgnore}"
	 *            default-value="false"
	 * 
	 */
	private boolean stopTestsOnIgnore;

	/**
	 * If true, the mojo will stop when it encountered an exception error
	 * message.
	 * 
	 * @parameter expression="${run-tests.stopTestsOnException}"
	 *            default-value="true"
	 * 
	 */
	private boolean stopTestsOnException;

	/**
	 * If true, the mojo will stop when it encountered a wrong error message.
	 * 
	 * @parameter expression="${run-tests.stopTestsOnWrong}"
	 *            default-value="true"
	 * 
	 */
	private boolean stopTestsOnWrong;

	/**
	 * A resultlistener class that listens to the outcome of tests. Should
	 * implement {@link fitnesse.responders.run.ResultsListener}
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
		final PluginManager pluginManager = PluginManagerFactory.getPluginManager(runnerPluginConfig);
		pluginManager.run();
	}

	/**
	 * Collect the plugin configuration settings
	 * 
	 * @return {@link nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.RunnerPluginConfig}
	 * @throws MojoExecutionException
	 */
	private RunnerPluginConfig getPluginConfig() throws MojoExecutionException {
		final Builder pluginConfigBuilder = PluginManagerFactory.getPluginConfigBuilder(RunnerPluginConfig.class);
		pluginConfigBuilder.setResultsListenerClass(resultsListenerClass);
		pluginConfigBuilder.setWikiRoot(this.wikiRoot);
		pluginConfigBuilder.setFitNesseOutputDirectory(this.fitnesseOutputDirectory);
		pluginConfigBuilder.setPort(this.port);
		pluginConfigBuilder.setStopTestsOnException(stopTestsOnException);
		pluginConfigBuilder.setStopTestsOnFailure(stopTestsOnFailure);
		pluginConfigBuilder.setStopTestsOnIgnore(stopTestsOnIgnore);
		pluginConfigBuilder.setStopTestsOnWrong(stopTestsOnWrong);
		pluginConfigBuilder.setTests(createList(tests));
		pluginConfigBuilder.setSuites(createList(suites));
		pluginConfigBuilder.setSuiteFilter(suiteFilter);
		pluginConfigBuilder.setSuitePageName(suitePageName);
		return pluginConfigBuilder.build();
	}

	private List<String> createList(final String[] array) {
		final List<String> list = new ArrayList<String>();
		if (array != null) {
			for (final String element : array) {
				list.add(element);
			}
		}
		return list;
	}

}
