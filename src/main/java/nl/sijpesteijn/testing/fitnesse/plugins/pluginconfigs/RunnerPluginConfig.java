package nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs;

import java.util.List;

import org.apache.maven.plugin.logging.Log;

/**
 * Runner plugin configuration.
 * 
 */
public class RunnerPluginConfig implements PluginConfig {

	private final String wikiRoot;
	private final String mafiaTestResultsDirectory;
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
	private final int retainDays;
	private final Log mavenLogger;

	public RunnerPluginConfig(final String wikiRoot, final String nameRootPage, final String mafiaTestResultsDirectory,
			final int port, final int retainDays, final String logDirectory, final Log log,
			final boolean stopTestsOnFailure, final boolean stopTestsOnIgnore, final boolean stopTestsOnException,
			final boolean stopTestsOnWrong, final List<String> tests, final List<String> suites,
			final String suiteFilter, final String suitePageName) {
		this.wikiRoot = wikiRoot;
		this.nameRootPage = nameRootPage;
		this.mafiaTestResultsDirectory = mafiaTestResultsDirectory;
		this.port = port;
		this.retainDays = retainDays;
		this.logDirectory = logDirectory;
		this.mavenLogger = log;
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
		private String mafiaTestResultsDirectory;
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
		private int retainDays;
		private Log mavenLogger;

		public Builder setWikiRoot(final String wikiRoot) {
			this.wikiRoot = wikiRoot;
			return this;
		}

		public Builder setMafiaTestResultsDirectory(final String mafiaTestResultsDirectory) {
			this.mafiaTestResultsDirectory = mafiaTestResultsDirectory;
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

		public Builder setMavenLogger(final Log mavenLogger) {
			this.mavenLogger = mavenLogger;
			return this;
		}

		public Builder setLogDirectory(final String logDirectory) {
			this.logDirectory = logDirectory;
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
			return new RunnerPluginConfig(wikiRoot, nameRootPage, mafiaTestResultsDirectory, port, retainDays,
					logDirectory, mavenLogger, stopTestsOnFailure, stopTestsOnIgnore, stopTestsOnException,
					stopTestsOnWrong, tests, suites, suiteFilter, suitePageName);
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

	public String getMafiaTestResultsDirectory() {
		return mafiaTestResultsDirectory;
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

	public Log getMavenLogger() {
		return mavenLogger;
	}

	/**
	 * Return the plugin configuration in one big string.
	 */
	@Override
	public String toString() {
		return "Port: " + this.port + ", Wiki Root: " + this.wikiRoot + ", Name Root Page: " + this.nameRootPage
				+ ", Retain days: " + this.retainDays + ", Log path: " + this.logDirectory
				+ ", Mafia Test Results Directory " + this.mafiaTestResultsDirectory + ", Stop Tests On Exception: "
				+ this.stopTestsOnException + ", Stop Tests On Failure: " + this.stopTestsOnFailure
				+ ", Stop Tests On Ignore: " + this.stopTestsOnIgnore + ", Stop Tests On Wrong: "
				+ this.stopTestsOnWrong + ", Tests: " + this.tests.toString() + ", Suites: " + this.suites.toString()
				+ ", SuiteFilter: " + this.suiteFilter + ", Suite Pagename: " + this.suitePageName;
	}

}
