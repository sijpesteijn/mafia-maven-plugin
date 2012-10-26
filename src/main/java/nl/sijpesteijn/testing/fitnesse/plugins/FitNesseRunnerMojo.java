package nl.sijpesteijn.testing.fitnesse.plugins;

import java.util.List;

import nl.sijpesteijn.testing.fitnesse.plugins.managers.RunnerPluginManager;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.RunnerPluginConfig;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Goal to run the Fitnesse tests.
 * 
 * @goal test
 * 
 * @phase integration-test
 */
public class FitNesseRunnerMojo extends AbstractMojo {

	/**
	 * The port number for FitNesse to run the tests.
	 * 
	 * @parameter expression="${test.port}" default-value="9091"
	 */
	private int port;

	/**
	 * Location of the wiki root directory.
	 * 
	 * @parameter expression="${test.wikiRoot}" default-value="${basedir}"
	 * @required
	 */
	private String wikiRoot;

	/**
	 * The name of the wiki root page.
	 * 
	 * @parameter expression="${test.nameRootPage}" default-value="FitNesseRoot"
	 */
	private String nameRootPage;

	/**
	 * The location for FitNesse to place the log files.
	 * 
	 * @parameter expression="${test.log}"
	 *            default-value="${basedir}/log/testlog/"
	 */
	private String logDirectory;

	/**
	 * The directory where the Fitnesse reports have been generated.
	 * 
	 * @parameter expression="${test.mafiaTestResultsDirectory}"
	 *            default-value="mafiaTestResults"
	 */
	private String mafiaTestResultsDirectory;

	/**
	 * Location of the local repository.
	 * <p>
	 * Note: This is passed by Maven and must not be configured by the user.
	 * </p>
	 * 
	 * @parameter expression="${settings.localRepository}"
	 * @readonly
	 * @required
	 */
	private String repositoryDirectory;

	/**
	 * List of test to be run.
	 * 
	 * @parameter expression="${test.tests}"
	 */
	private List<String> tests;

	/**
	 * List of suites to be run.
	 * 
	 * @parameter expression="${test.suites}"
	 */
	private List<String> suites;

	/**
	 * Name of the suite page name.
	 * 
	 * @parameter expression="${test.suitePageName}"
	 */
	private String suitePageName;

	/**
	 * Suite filter to run in the specified suite (=suitePageName).
	 * 
	 * @parameter expression="${test.suiteFilter}"
	 */
	private String suiteFilter;

	/**
	 * If true, the mojo will stop when it encountered a failure error message.
	 * 
	 * @parameter expression="${test.stopTestsOnFailure}" default-value="true"
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
	 * @parameter expression="${test.stopTestsOnException}" default-value="true"
	 * 
	 */
	private boolean stopTestsOnException;

	/**
	 * If true, the mojo will stop when it encountered a wrong error message.
	 * 
	 * @parameter expression="${test.stopTestsOnWrong}" default-value="true"
	 * 
	 */
	private boolean stopTestsOnWrong;

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		final RunnerPluginConfig runnerPluginConfig = getPluginConfig();
		getLog().debug("Runner config: " + runnerPluginConfig.toString());
		new RunnerPluginManager(runnerPluginConfig).run();
		getLog().info("Finished running tests and suites.");
	}

	/**
	 * Collect the plugin configuration settings
	 * 
	 * @return {@link nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.RunnerPluginConfig}
	 * @throws MojoExecutionException
	 */
	private RunnerPluginConfig getPluginConfig() throws MojoExecutionException {
		return new RunnerPluginConfig(wikiRoot, nameRootPage, repositoryDirectory, logDirectory, port, port, getLog(),
				mafiaTestResultsDirectory, stopTestsOnFailure, stopTestsOnIgnore, stopTestsOnException,
				stopTestsOnWrong, tests, suites, suiteFilter, suitePageName);
	}

}
