package nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs;

import java.util.List;
import java.util.ResourceBundle;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.logging.Log;

/**
 * Reporter plugin configuration.
 * 
 */
public class ReporterPluginConfig extends BasePluginConfig {

	private final String outputDirectory;
	private final String mafiaTestResultsDirectory;
	private final Sink sink;
	private final ResourceBundle resourceBundle;
	private final List<String> tests;
	private final List<String> suites;
	private final String suiteFilter;
	private final String suitePageName;
	private final String testName;

	public ReporterPluginConfig(final String wikiRoot, final String nameRootPage, final String repositoryDirectory,
			final String logDirectory, final int fitnessePort, final int retainDays, final Log mavenLogger,
			final String outputDirectory, final String testName, final String mafiaTestResultsDirectory,
			final Sink sink, final ResourceBundle resourceBundle, final List<String> tests, final List<String> suites,
			final String suiteFilter, final String suitePageName) {
		super(wikiRoot, nameRootPage, repositoryDirectory, logDirectory, fitnessePort, retainDays, mavenLogger);
		this.outputDirectory = outputDirectory;
		this.testName = testName;
		this.mafiaTestResultsDirectory = mafiaTestResultsDirectory;
		this.sink = sink;
		this.resourceBundle = resourceBundle;
		this.tests = tests;
		this.suites = suites;
		this.suiteFilter = suiteFilter;
		this.suitePageName = suitePageName;
	}

	public String getOutputDirectory() {
		return this.outputDirectory;
	}

	public String getMafiaTestResultsDirectory() {
		return mafiaTestResultsDirectory;
	}

	public Sink getSink() {
		return sink;
	}

	public ResourceBundle getResourceBundle() {
		return resourceBundle;
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

	public String getTestName() {
		return testName;
	}

	/**
	 * Return plugin configuration in one big string.
	 */
	@Override
	public String toString() {
		return "Wiki root: " + getWikiRoot() + ", Name of root page: " + getNameRootPage()
				+ ", Mafia Testresults directory: " + this.mafiaTestResultsDirectory + ",Output directory: "
				+ this.outputDirectory + ", TestName: " + this.testName + ", Sink: " + this.sink.toString()
				+ ", ResourceBundle: " + resourceBundle.toString() + ", Tests: " + this.tests.toString() + ", Suites: "
				+ this.suites.toString() + ", SuiteFilter: " + this.suiteFilter + ", Suite Pagename: "
				+ this.suitePageName;
	}
}
