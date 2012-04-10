package nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs;

import java.util.List;
import java.util.ResourceBundle;

import org.apache.maven.doxia.sink.Sink;

/**
 * Reporter plugin configuration.
 * 
 */
public class ReporterPluginConfig implements PluginConfig {

    private final String outputDirectory;
    private final String mafiaTestResultsDirectory;
    private final Sink sink;
    private final ResourceBundle resourceBundle;
    private final List<String> tests;
    private final List<String> suites;
    private final String suiteFilter;
    private final String suitePageName;
    private final String name;

    public ReporterPluginConfig(final String outputDirectory, final String name,
                                final String mafiaTestResultsDirectory, final Sink sink,
                                final ResourceBundle resourceBundle, final List<String> tests,
                                final List<String> suites, final String suiteFilter, final String suitePageName)
    {
        this.outputDirectory = outputDirectory;
        this.name = name;
        this.mafiaTestResultsDirectory = mafiaTestResultsDirectory;
        this.sink = sink;
        this.resourceBundle = resourceBundle;
        this.tests = tests;
        this.suites = suites;
        this.suiteFilter = suiteFilter;
        this.suitePageName = suitePageName;
    }

    /**
     * The builder for this configuration
     * 
     */
    public static class Builder implements PluginConfigBuilder {

        private String mafiaTestResultsDirectory;
        private String outputDirectory;
        private Sink sink;
        private ResourceBundle resourceBundle;
        private List<String> tests;
        private List<String> suites;
        private String suiteFilter;
        private String suitePageName;
        private String name;

        public void setMafiaTestResultsDirectory(final String mafiaTestResultsDirectory) {
            this.mafiaTestResultsDirectory = mafiaTestResultsDirectory;
        }

        public void setOutputDirectory(final String outputDirectory) {
            this.outputDirectory = outputDirectory;
        }

        public ReporterPluginConfig build() {
            return new ReporterPluginConfig(this.outputDirectory, this.name, this.mafiaTestResultsDirectory, this.sink,
                this.resourceBundle, this.tests, this.suites, this.suiteFilter, this.suitePageName);
        }

        public void setSink(final Sink sink) {
            this.sink = sink;
        }

        public void setResourceBundle(final ResourceBundle resourceBundle) {
            this.resourceBundle = resourceBundle;
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

        public void setName(final String name) {
            this.name = name;
        }

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

    public String getName() {
        return name;
    }

    /**
     * Return plugin configuration in one big string.
     */
    @Override
    public String toString() {
        return "Mafia Testresults directory: " + this.mafiaTestResultsDirectory + ",Output directory: "
                + this.outputDirectory + ", Name: " + this.name + ", Sink: " + this.sink.toString()
                + ", ResourceBundle: " + resourceBundle.toString() + ", Tests: " + this.tests.toString() + ", Suites: "
                + this.suites.toString() + ", SuiteFilter: " + this.suiteFilter + ", Suite Pagename: "
                + this.suitePageName;
    }

}
