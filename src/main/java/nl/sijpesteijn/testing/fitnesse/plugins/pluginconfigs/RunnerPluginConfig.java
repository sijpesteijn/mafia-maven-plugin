package nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs;

import java.util.List;

import org.apache.maven.plugin.logging.Log;

/**
 * Runner plugin configuration.
 * 
 */
public class RunnerPluginConfig implements PluginConfig {

	private final String wikiRoot;
	private final String fitnesseOutputDirectory;
	private final int port;
	private final boolean stopTestsOnFailure;
	private final boolean stopTestsOnIgnore;
	private final boolean stopTestsOnException;
	private final boolean stopTestsOnWrong;
	private final List<String> tests;
	private final List<String> suites;
	private final String suiteFilter;
	private final String suitePageName;
	private final String nameRootPage;
	private final String logDirectory;
	private final String testResultsDirectory;
	private final int retainDays;
	private final Log log;

	public RunnerPluginConfig(final String wikiRoot, final String nameRootPage, final String fitnesseOutputDirectory,
			final int port, final int retainDays, final String logDirectory, final String testResultsDirecotory,
			final Log log, final boolean stopTestsOnFailure, final boolean stopTestsOnIgnore,
			final boolean stopTestsOnException, final boolean stopTestsOnWrong, final List<String> tests,
			final List<String> suites, final String suiteFilter, final String suitePageName) {
		this.wikiRoot = wikiRoot;
		this.nameRootPage = nameRootPage;
		this.fitnesseOutputDirectory = fitnesseOutputDirectory;
		this.port = port;
		this.retainDays = retainDays;
		this.logDirectory = logDirectory;
		testResultsDirectory = testResultsDirecotory;
		this.log = log;
		this.stopTestsOnFailure = stopTestsOnFailure;
		this.stopTestsOnIgnore = stopTestsOnIgnore;
		this.stopTestsOnException = stopTestsOnException;
		this.stopTestsOnWrong = stopTestsOnWrong;
		this.tests = tests;
		this.suites = suites;
		this.suiteFilter = suiteFilter;
		this.suitePageName = suitePageName;
	}

	/**
	 * The builder for this configuration.
	 * 
	 */
	public static class Builder implements PluginConfigBuilder {

		private String wikiRoot;
		private String fitnesseOutputDirectory;
		private int port;
		private boolean stopTestsOnFailure;
		private boolean stopTestsOnIgnore;
		private boolean stopTestsOnException;
		private boolean stopTestsOnWrong;
		private List<String> tests;
		private List<String> suites;
		private String suiteFilter;
		private String suitePageName;
		private String nameRootPage;
		private String logDirectory;
		private String testResultsDirectory;
		private int retainDays;
		private Log log;

		public Builder setWikiRoot(final String wikiRoot) {
			this.wikiRoot = wikiRoot;
			return this;
		}

		public Builder setFitNesseOutputDirectory(final String fitnesseOutputDirectory) {
			this.fitnesseOutputDirectory = fitnesseOutputDirectory;
			return this;
		}

		public Builder setPort(final int port) {
			this.port = port;
			return this;
		}

		public Builder setRetainDays(final int retainDays) {
			this.retainDays = retainDays;
			return this;
		}

		public Builder setLog(final Log log) {
			this.log = log;
			return this;
		}

		public Builder setLogDirectory(final String logDirectory) {
			this.logDirectory = logDirectory;
			return this;
		}

		public Builder setTestResultsDirectory(final String testResultsDirectory) {
			this.testResultsDirectory = testResultsDirectory;
			return this;
		}

		public Builder setStopTestsOnException(final boolean stopTestsOnException) {
			this.stopTestsOnException = stopTestsOnException;
			return this;
		}

		public Builder setStopTestsOnFailure(final boolean stopTestsOnFailure) {
			this.stopTestsOnFailure = stopTestsOnFailure;
			return this;
		}

		public Builder setStopTestsOnIgnore(final boolean stopTestsOnIgnore) {
			this.stopTestsOnIgnore = stopTestsOnIgnore;
			return this;
		}

		public Builder setStopTestsOnWrong(final boolean stopTestsOnWrong) {
			this.stopTestsOnWrong = stopTestsOnWrong;
			return this;
		}

		public RunnerPluginConfig build() {
			return new RunnerPluginConfig(wikiRoot, nameRootPage, fitnesseOutputDirectory, port, retainDays,
					logDirectory, testResultsDirectory, log, stopTestsOnFailure, stopTestsOnIgnore,
					stopTestsOnException, stopTestsOnWrong, tests, suites, suiteFilter, suitePageName);
		}

		public void setTests(final List<String> tests) {
			this.tests = tests;
		}

		public void setSuites(final List<String> suites) {
			this.suites = suites;
		}

		public void setSuiteFilter(final String suiteFilter) {
			this.suiteFilter = suiteFilter;
		}

		public void setSuitePageName(final String suitePageName) {
			this.suitePageName = suitePageName;
		}

		public void setNameRootPage(final String nameRootPage) {
			this.nameRootPage = nameRootPage;
		}

	}

	public String getWikiRoot() {
		return wikiRoot;
	}

	public String getNameRootPage() {
		return nameRootPage;
	}

	public String getFitnesseOutputDirectory() {
		return fitnesseOutputDirectory;
	}

	public int getPort() {
		return port;
	}

	public int getRetainDays() {
		return retainDays;
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

	public String getLogDirectory() {
		return logDirectory;
	}

	public String getTestResultsDirectory() {
		return testResultsDirectory;
	}

	public Log getLog() {
		return log;
	}

	/**
	 * Return the plugin configuration in one big string.
	 */
	@Override
	public String toString() {
		return "FitNesse Output Directory: " + this.fitnesseOutputDirectory + ", Port: " + this.port + ", Wiki Root: "
				+ this.wikiRoot + ", Name Root page: " + this.nameRootPage + ",Log Direcotry" + this.logDirectory
				+ "Test Results Directory " + this.testResultsDirectory + ", Stop Tests On Exception: "
				+ this.stopTestsOnException + ", Stop Tests On Failure: " + this.stopTestsOnFailure
				+ ", Stop Tests On Ignore: " + this.stopTestsOnIgnore + ", Stop Tests On Wrong: "
				+ this.stopTestsOnWrong + ", Tests: " + this.tests.toString() + ", Suites: " + this.suites.toString()
				+ ", SuiteFilter: " + this.suiteFilter + ", Suite Pagename: " + this.suitePageName;
	}

}
