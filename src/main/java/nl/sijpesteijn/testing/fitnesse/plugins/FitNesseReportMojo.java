package nl.sijpesteijn.testing.fitnesse.plugins;

import nl.sijpesteijn.testing.fitnesse.plugins.report.*;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaException;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * This mojo will collect the test results from the run tests.
 */
@Mojo(name = "report")
public class FitNesseReportMojo extends AbstractMavenReport {

    /**
     * Root path in the report resources.
     */
    private static final String PLUGIN_RESOURCES = "nl/sijpesteijn/testing/fitnesse/plugins/resources/";

    /**
     * @link {org.apache.maven.project.MavenProject}
     */
    @Component
    private MavenProject project;

    /**
     * @link {nl.sijpesteijn.testing.fitnesse.plugins.report.ReportFormatter}
     */
    @Component(role = ReportFormatter.class)
    private ReportFormatter reportFormatter;

    /**
     * SiteRenderer.
     */
    @Component
    private Renderer siteRenderer;

    /**
     * Report outputdirectory.
     */
    @Parameter(property = "outputDirectory", defaultValue = "${project.reporting.outputDirectory}/")
    private String outputDirectory;

    /**
     * The site renderer.
     *
     * @return - the site renderer.
     */
    @Override
    protected final Renderer getSiteRenderer() {
        return this.siteRenderer;
    }

    /**
     * The output directory of the report location.
     *
     * @return - path to the report location.
     */
    @Override
    protected final String getOutputDirectory() {
        return outputDirectory;
    }

    /**
     * The maven project.
     *
     * @return - the maven proejct.
     */
    @Override
    protected final MavenProject getProject() {
        return project;
    }

    /**
     * The location of the wiki root directory.
     */
    @Parameter(property = "wikiRoot", defaultValue = "${basedir}/")
    private String wikiRoot;

    /**
     * The name of the wiki root page.
     */
    @Parameter(property = "nameRootPage", defaultValue = "FitNesseRoot")
    private String nameRootPage;

    /**
     * Create the mafia report.
     *
     * @param locale - the locale to use.
     * @throws MavenReportException - unable to generate report.
     */
    @Override
    protected final void executeReport(final Locale locale) throws MavenReportException {
        final String mafiaTestResultsDir = wikiRoot + nameRootPage + "/files/mafiaResults/";
        getLog().debug(toString());
        try {
            FileUtils.copyDirectoryStructure(new File(mafiaTestResultsDir), new File(outputDirectory));
            copyResources();
            final MafiaTestResultRepository resultRepository =
                    new MafiaTestResultRepository(new File(outputDirectory));
            final MafiaIndexGenerator generator =
                    new MafiaIndexGenerator(resultRepository, getBundle(locale), getSink(),
                            getTestSummary(mafiaTestResultsDir + "mafiaresults.properties"),
                            reportFormatter);
            generator.generate();
        } catch (IOException e) {
            throw new MavenReportException("Could not generate report.", e);
        } catch (MafiaException e) {
            throw new MavenReportException("Could not generate report." + e.getMessage(), e);
        }

    }

    /**
     * Copy the mafia report resources to the report location.
     *
     * @throws IOException - unable to copy resources.
     */
    private void copyResources() throws IOException {
        final ReportResource resource = new ReportResource(outputDirectory, PLUGIN_RESOURCES);
        resource.copy("css/");
        resource.copy("images/");
        resource.copy("javascript/");
        resource.copy("wysiwyg/");
    }

    /**
     * Find the resource bundle for the locale.
     *
     * @param locale - the local to use.
     * @return - a resource bundle.
     */
    private ResourceBundle getBundle(final Locale locale) {
        return ResourceBundle.getBundle("mafia-report", locale, Thread.currentThread().getContextClassLoader());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getOutputName() {
        return "mafia-fitnesse-report";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getName(final Locale locale) {
        return "FitNesse test results";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getDescription(final Locale locale) {
        return "Report created with Mafia plugin";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return "WikiRoot: " + wikiRoot + ", Name root page: " + nameRootPage + ", Output directory: " + outputDirectory;
    }

    /**
     * Create a mafia test summary from a fitnesse test result file.
     *
     * @param mafiaTestResultDir - fitnesse test result file.
     * @return - mafia test summary.
     * @throws IOException - unable to read fitnesse test result file.
     */
    private MafiaTestSummary getTestSummary(final String mafiaTestResultDir) throws IOException {
        final Properties properties = new Properties();
        final InputStream is = new FileInputStream(mafiaTestResultDir);
        try {
            properties.load(is);
            final MafiaTestSummary summary = new MafiaTestSummary();
            summary.wrong = Integer.parseInt(properties.getProperty("wrong"));
            summary.right = Integer.parseInt(properties.getProperty("right"));
            summary.ignores = Integer.parseInt(properties.getProperty("ignores"));
            summary.exceptions = Integer.parseInt(properties.getProperty("exceptions"));
            summary.setTestTime(Long.parseLong(properties.getProperty("testTime")));
            summary.setRunDate(properties.getProperty("runDate"));
            return summary;
        } finally {
            is.close();
        }
    }
}
