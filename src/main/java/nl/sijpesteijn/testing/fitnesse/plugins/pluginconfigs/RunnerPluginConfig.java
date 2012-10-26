package nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs;

import java.util.List;

import org.apache.maven.plugin.logging.Log;

/**
 * Runner plugin configuration.
 * 
 */
public class RunnerPluginConfig extends BasePluginConfig {

	private final String mafiaTestResultsDirectory;
	private final boolean stopTestsOnFailure;
	private final boolean stopTestsOnIgnore;
	private final boolean stopTestsOnException;
	private final boolean stopTestsOnWrong;
	private final List<String> tests;
	private final List<String> suites;
	private final String suiteFilter;
	private final String suitePageName;

	public RunnerPluginConfig(final String wikiRoot, final String nameRootPage, final String repositoryDirectory,
			final String logDirectory, final int fitnessePort, final int retainDays, final Log mavenLogger,
			final String mafiaTestResultsDirectory, final boolean stopTestsOnFailure, final boolean stopTestsOnIgnore,
			final boolean stopTestsOnException, final boolean stopTestsOnWrong, final List<String> tests,
			final List<String> suites, final String suiteFilter, final String suitePageName) {
		super(wikiRoot, nameRootPage, repositoryDirectory, logDirectory, fitnessePort, retainDays, mavenLogger);
		this.mafiaTestResultsDirectory = mafiaTestResultsDirectory;
		this.stopTestsOnFailure = stopTestsOnFailure;
		this.stopTestsOnIgnore = stopTestsOnIgnore;
		this.stopTestsOnException = stopTestsOnException;
		this.stopTestsOnWrong = stopTestsOnWrong;
		this.tests = tests;
		this.suites = suites;
		this.suiteFilter = suiteFilter;
		this.suitePageName = suitePageName;
	}

	public String getMafiaTestResultsDirectory() {
		return mafiaTestResultsDirectory;
	}

	public boolean isStopTestsOnFailure() {
		return stopTestsOnFailure;
	}

	public boolean isStopTestsOnException() {
		return stopTestsOnException;
	}

	public boolean isStopTestsOnIgnore() {
		return stopTestsOnIgnore;
	}

	public boolean isStopTestsOnWrong() {
		return stopTestsOnWrong;
	}

	public List<String> getTests() {
		return tests;
	}

	public List<String> getSuites() {
		return suites;
	}

	public String getSuiteFilter() {
		return suiteFilter;
	}

	public String getSuitePageName() {
		return suitePageName;
	}

	/**
	 * Return the plugin configuration in one big string.
	 */
	// @Override
	// public String toString() {
	// return "Port: " + this.port + ", Wiki Root: " + this.wikiRoot +
	// ", Name Root Page: " + this.nameRootPage
	// + ", Retain days: " + this.retainDays + ", Log path: " +
	// this.logDirectory
	// + ", Mafia Test Results Directory: " + this.mafiaTestResultsDirectory +
	// ", Repository directory: "
	// + this.repositoryDirectory + ", Stop Tests On Exception: " +
	// this.stopTestsOnException
	// + ", Stop Tests On Failure: " + this.stopTestsOnFailure +
	// ", Stop Tests On Ignore: "
	// + this.stopTestsOnIgnore + ", Stop Tests On Wrong: " +
	// this.stopTestsOnWrong + ", Tests: "
	// + this.tests.toString() + ", Suites: " + this.suites.toString() +
	// ", SuiteFilter: " + this.suiteFilter
	// + ", Suite Pagename: " + this.suitePageName;
	// }

}
