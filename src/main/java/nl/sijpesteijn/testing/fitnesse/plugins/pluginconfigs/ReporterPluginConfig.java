package nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs;

import java.io.File;

public class ReporterPluginConfig implements PluginConfig {

    private final File fitnesseOutputDirectory;
    private final File outputDirectory;
    private final String reportTemplate;

    public ReporterPluginConfig(final File fitnesseOutputDirectory, final File outputDirectory,
                                final String reportTemplate)
    {
        this.fitnesseOutputDirectory = fitnesseOutputDirectory;
        this.outputDirectory = outputDirectory;
        this.reportTemplate = reportTemplate;
    }

    public static class Builder {

        private File outputDirectory;
        private File fitnesseOutputDirectory;
        private String reportTemplate;

        public void setOutputDirectory(final File outputDirectory) {
            this.outputDirectory = outputDirectory;
        }

        public void setFitnesseOutputDirectory(final File fitnesseOutputDirectory) {
            this.fitnesseOutputDirectory = fitnesseOutputDirectory;
        }

        public void setReportTemplate(final String reportTemplate) {
            this.reportTemplate = reportTemplate;
        }

        public ReporterPluginConfig build() {
            return new ReporterPluginConfig(this.fitnesseOutputDirectory, this.outputDirectory, this.reportTemplate);
        }

    }

    public File getFitnesseOutputDirectory() {
        return this.fitnesseOutputDirectory;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public String getReportTemplate() {
        return this.reportTemplate;
    }

    @Override
    public String toString() {
        return "FitNesse Output Directory: " + this.fitnesseOutputDirectory + ", Output directory: "
                + this.outputDirectory + ", Report template: " + this.reportTemplate;
    }
}
