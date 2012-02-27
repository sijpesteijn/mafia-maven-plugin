package nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs;

/**
 * Reporter plugin configuration.
 * 
 */
public class ReporterPluginConfig implements PluginConfig {

	private final String fitnesseReportDirectory;
	private final String testResultsDirectory;
	private final String reportTemplate;

	public ReporterPluginConfig(final String fitnesseOutputDirectory, final String testResultsDirectory,
			final String reportTemplate) {
		this.fitnesseReportDirectory = fitnesseOutputDirectory;
		this.testResultsDirectory = testResultsDirectory;
		this.reportTemplate = reportTemplate;
	}

	/**
	 * The builder for this configuration
	 * 
	 */
	public static class Builder implements PluginConfigBuilder {

		private String testResultsDirectory;
		private String fitnesseReportDirectory;
		private String reportTemplate;

		public void setTestResultsDirectory(final String testResultsDirectory) {
			this.testResultsDirectory = testResultsDirectory;
		}

		public void setFitnesseReportDirectory(final String fitnesseReportDirectory) {
			this.fitnesseReportDirectory = fitnesseReportDirectory;
		}

		public void setReportTemplate(final String reportTemplate) {
			this.reportTemplate = reportTemplate;
		}

		public ReporterPluginConfig build() {
			return new ReporterPluginConfig(this.fitnesseReportDirectory, this.testResultsDirectory,
					this.reportTemplate);
		}

	}

	public String getFitnesseReportDirectory() {
		return this.fitnesseReportDirectory;
	}

	public String getTestResultsDirectory() {
		return testResultsDirectory;
	}

	public String getReportTemplate() {
		return this.reportTemplate;
	}

	/**
	 * Return plugin configuration in one big string.
	 */
	@Override
	public String toString() {
		return "Testresults directory: " + this.testResultsDirectory + ",FitNesse report directory: "
				+ this.fitnesseReportDirectory + ", Report template: " + this.reportTemplate;
	}
}
