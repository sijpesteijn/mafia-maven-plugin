package nl.sijpesteijn.testing.fitnesse.plugins;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.plexus.util.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This mojo will collect the test results from the run tests.
 */
@Mojo(name = "report", requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM)
public class FitNesseReportMojo extends AbstractMavenReport {

    /**
     * @link {org.apache.maven.project.MavenProject}
     */
    @Component
    private MavenProject project;

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
        getLog().debug(toString());
        final File reportDirectory = new File(wikiRoot + nameRootPage + "/files/mafiaResults/report/");
        if (reportDirectory.exists()) {
            try {
                File outputDir = new File(outputDirectory);
                File cssOutput = new File(outputDir, "css");
                FileUtils.copyDirectory(new File(reportDirectory, "css"), cssOutput);
                FileUtils.copyDirectory(new File(reportDirectory, "javascript"), new File(outputDir, "javascript"));
                FileUtils.copyDirectory(new File(reportDirectory, "images"), new File(outputDir, "images"));
                Sink sink = getSink();
                ResourceBundle bundle = getBundle(locale);
                Document document = Jsoup.parse(new File(reportDirectory, "fitnesse_report.html"), "UTF-8");
                Element head = document.head();
                Elements links = head.select("link");
                Elements scripts = head.select("script");
                Element body = document.body();

                sink.head();
                sink.title();
                sink.text(bundle.getString("report.mafia.title"));
                sink.title_();
                sink.head_();

                sink.body();
                for(int i =0;i<links.size();i++) {
                    sink.rawText(links.get(i).outerHtml());
                }
                for(int i =0;i<scripts.size();i++) {
                    sink.rawText(scripts.get(i).outerHtml());
                }

                sink.rawText(body.html());
                sink.text(bundle.getString("report.mafia.moreInfo") + " ");
                sink.link(bundle.getString("report.mafia.moreInfo.link"));
                sink.text(bundle.getString("report.mafia.moreInfo.linkName"));
                sink.link_();
                sink.rawText(".");
                sink.body_();
                sink.flush();
            } catch (IOException e) {
                throw new MavenReportException("Could not generate report.", e);
            }
        }
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
        return "fitnesse-report";
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
}
