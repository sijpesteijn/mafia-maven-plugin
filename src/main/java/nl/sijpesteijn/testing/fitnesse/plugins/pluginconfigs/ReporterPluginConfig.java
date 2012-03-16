package nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs;

import java.util.ResourceBundle;

import org.apache.maven.doxia.sink.Sink;

/**
 * Reporter plugin configuration.
 * 
 */
public class ReporterPluginConfig implements PluginConfig {

    private final String fitnesseReportDirectory;
    private final String testResultsDirectory;
    private final Sink sink;
    private final ResourceBundle resourceBundle;

    public ReporterPluginConfig(final String fitnesseOutputDirectory, final String testResultsDirectory,
            final Sink sink, final ResourceBundle resourceBundle) {
        this.fitnesseReportDirectory = fitnesseOutputDirectory;
        this.testResultsDirectory = testResultsDirectory;
        this.sink = sink;
        this.resourceBundle = resourceBundle;
    }

    /**
     * The builder for this configuration
     * 
     */
    public static class Builder implements PluginConfigBuilder {

        private String testResultsDirectory;
        private String fitnesseReportDirectory;
        private Sink sink;
        private ResourceBundle resourceBundle;

        public void setTestResultsDirectory(final String testResultsDirectory) {
            this.testResultsDirectory = testResultsDirectory;
        }

        public void setFitnesseReportDirectory(final String fitnesseReportDirectory) {
            this.fitnesseReportDirectory = fitnesseReportDirectory;
        }

        public ReporterPluginConfig build() {
            return new ReporterPluginConfig(this.fitnesseReportDirectory, this.testResultsDirectory, this.sink,
                    this.resourceBundle);
        }

        public void setSink(final Sink sink) {
            this.sink = sink;
        }

        public void setResourceBundle(final ResourceBundle resourceBundle) {
            this.resourceBundle = resourceBundle;
        }

    }

    public String getFitnesseTestResultsDirectory() {
        return this.fitnesseReportDirectory;
    }

    public String getTestResultsDirectory() {
        return testResultsDirectory;
    }

    public Sink getSink() {
        return sink;
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    /**
     * Return plugin configuration in one big string.
     */
    @Override
    public String toString() {
        return "Testresults directory: " + this.testResultsDirectory + ",FitNesse report directory: "
                + this.fitnesseReportDirectory + ", Sink: " + this.sink.toString() + ", ResourceBundle: "
                + resourceBundle.toString();
    }

}
