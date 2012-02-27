package nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs;

import java.util.List;

/**
 * Runner plugin configuration.
 * 
 */
public class RunnerPluginConfig implements PluginConfig {

	private final String wikiRoot;
	private final String resultsListenerClass;
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

	public RunnerPluginConfig(final String wikiRoot, final String nameRootPage, final String resultsListenerClass,
			final String fitnesseOutputDirectory, final int port, final boolean stopTestsOnFailure,
			final boolean stopTestsOnIgnore, final boolean stopTestsOnException, final boolean stopTestsOnWrong,
			final List<String> tests, final List<String> suites, final String suiteFilter, final String suitePageName) {
		this.wikiRoot = wikiRoot;
		this.nameRootPage = nameRootPage;
		this.resultsListenerClass = resultsListenerClass;
		this.fitnesseOutputDirectory = fitnesseOutputDirectory;
		this.port = port;
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

		private String resultsListenerClass;
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

		public Builder setResultsListenerClass(final String resultsListenerClass) {
			this.resultsListenerClass = resultsListenerClass;
			return this;
		}

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
			return new RunnerPluginConfig(wikiRoot, nameRootPage, resultsListenerClass, fitnesseOutputDirectory, port,
					stopTestsOnFailure, stopTestsOnIgnore, stopTestsOnException, stopTestsOnWrong, tests, suites,
					suiteFilter, suitePageName);
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

	public String getResultsListenerClass() {
		return resultsListenerClass;
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

	public boolean isResultsListenerClassSet() {
		return resultsListenerClass != null && !resultsListenerClass.equals("");
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
	@Override
	public String toString() {
		return "FitNesse Output Directory: " + this.fitnesseOutputDirectory + ", Port: " + this.port
				+ ", Result Listener Class: " + this.resultsListenerClass + ", Wiki Root: " + this.wikiRoot
				+ ", Name Root page: " + this.nameRootPage + ", Stop Tests On Exception: " + this.stopTestsOnException
				+ ", Stop Tests On Failure: " + this.stopTestsOnFailure + ", Stop Tests On Ignore: "
				+ this.stopTestsOnIgnore + ", Stop Tests On Wrong: " + this.stopTestsOnWrong + ", Tests: "
				+ this.tests.toString() + ", Suites: " + this.suites.toString() + ", SuiteFilter: " + this.suiteFilter
				+ ", Suite Pagename: " + this.suitePageName;
	}
}
