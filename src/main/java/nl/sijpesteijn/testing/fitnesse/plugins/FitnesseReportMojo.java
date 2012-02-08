package nl.sijpesteijn.testing.fitnesse.plugins;

import java.io.File;
import java.util.Locale;

import nl.sijpesteijn.testing.fitnesse.plugins.managers.PluginManager;
import nl.sijpesteijn.testing.fitnesse.plugins.managers.PluginManagerFactory;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.ReporterPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.ReporterPluginConfig.Builder;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

/**
 * 
 * @author gijs
 * 
 * @phase post-site
 * @goal collect-report
 */
public class FitnesseReportMojo extends AbstractMavenReport {

    /**
     * The Maven project instance for the executing project.
     * <p>
     * Note: This is passed by Maven and must not be configured by the user.
     * </p>
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * <p>
     * Note: This is passed by Maven and must not be configured by the user.
     * </p>
     * 
     * @component
     */
    private Renderer siteRenderer;

    /**
     * The directory where the Fitnesse report will be generated. (Temporary storage)
     * 
     * @parameter expression="${report.workDirectory}" default-value="${project.build.directory}/fitnesse"
     * @required
     */
    private File workDirectory;

    /**
     * The directory where the Fitnesse reports will be placed.
     * 
     * @parameter expression="${report.fitnesseReportDirectory}"
     *            default-value="${project.build.directory}/site/fitnesse"
     */
    private File fitnesseOutputDirectory;

    /**
     * The report template
     * 
     * @parameter expression="${report.reportTemplate}"
     */
    private String reportTemplate;

    /**
     * Generate the FitNesse report
     */
    @Override
    protected void executeReport(final Locale arg0) throws MavenReportException {
        try {
            final ReporterPluginConfig reporterPluginConfig = getPluginConfig();
            getLog().info("Report config: " + reporterPluginConfig.toString());
            final PluginManager pluginManager = PluginManagerFactory.getPluginManager(reporterPluginConfig);
            pluginManager.run();
        } catch (final MojoExecutionException e) {
            throw new MavenReportException("" + e);
        } catch (final MojoFailureException e) {
            throw new MavenReportException("" + e);
        }
    }

    private ReporterPluginConfig getPluginConfig() throws MojoExecutionException {
        final Builder builder = PluginManagerFactory.getPluginConfigBuilder(ReporterPluginConfig.class);
        builder.setOutputDirectory(this.workDirectory);
        builder.setFitnesseOutputDirectory(this.fitnesseOutputDirectory);
        builder.setReportTemplate(this.reportTemplate);
        return builder.build();
    }

    /**
     * @return Get the report output directory. Passed by Maven.
     */
    @Override
    protected String getOutputDirectory() {
        return this.workDirectory.getAbsoluteFile().toString();
    }

    /**
     * @return Get the mavenproject. Passed by Maven
     */
    @Override
    protected MavenProject getProject() {
        return this.project;
    }

    /**
     * @return Get the site renderer. Passed by Maven.
     */
    @Override
    protected Renderer getSiteRenderer() {
        return this.siteRenderer;
    }

    protected void setFitnesseOutputDirectory(final File fitnesseOutputDirectory) {
        this.fitnesseOutputDirectory = fitnesseOutputDirectory;
    }

    protected void setOutputDirectory(final File outputDirectory) {
        this.workDirectory = outputDirectory;
    }

    @Override
    public String getDescription(final Locale arg0) {
        return "Maven mafia plugin - reporting: Generate a report of the fitnessetests that have run";
    }

    @Override
    public String getName(final Locale arg0) {
        return "Maven mafia plugin - reporting";
    }

    /**
     * @return the output name of this report.
     */
    @Override
    public String getOutputName() {
        return "fitnesse";
    }

}
