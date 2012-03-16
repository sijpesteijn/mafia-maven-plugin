package nl.sijpesteijn.testing.fitnesse.plugins.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.ReporterPluginConfig;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.plexus.util.FileUtils;

import fitnesse.responders.testHistory.PageHistory;
import fitnesse.responders.testHistory.TestHistory;

/**
 * Plugin manager responsible for collecting report results.
 * 
 */
public class ReporterPluginManager implements PluginManager {

    private final ReporterPluginConfig reporterPluginConfig;

    /**
     * 
     * @param reporterPluginConfig
     *            {@link nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.ReporterPluginConfig}
     */
    public ReporterPluginManager(final ReporterPluginConfig reporterPluginConfig) {
        this.reporterPluginConfig = reporterPluginConfig;
    }

    /**
     * Collect the reports
     * 
     * @throws MavenReportException
     */
    @Override
    public void run() throws MojoFailureException, MojoExecutionException {
        try {
            final List<String> pageNames = getPageNamesFromTestResultDirectory();
            final List<MafiaTestResult> mafiaTestResults = getMafiaTestResults(pageNames);

            final MafiaReportGenerator generator = new MafiaReportGenerator(reporterPluginConfig.getSink(),
                    reporterPluginConfig.getResourceBundle(), reporterPluginConfig, mafiaTestResults);
            generator.generate();
        } catch (final MavenReportException e) {
            new MojoExecutionException("Could not generate mafia report: ", e);
        } catch (final IOException e) {
            new MojoExecutionException("Could not generate mafia report: ", e);
        }
    }

    private List<String> getPageNamesFromTestResultDirectory() throws MojoFailureException {
        try {
            final File testResultDirectory = new File(reporterPluginConfig.getTestResultsDirectory() + "/xml/");
            final List<String> validDirectoryNames = new ArrayList<String>();
            @SuppressWarnings("unchecked")
            final List<String> directoryNames = FileUtils.getDirectoryNames(testResultDirectory, null, null, false);
            for (final String directoryName : directoryNames) {
                if (isValidDirectory(directoryName)) {
                    validDirectoryNames.add(directoryName);
                }
            }
            return validDirectoryNames;
        } catch (final IOException e) {
            throw new MojoFailureException("Error copying fitnesse reports.", e);
        }
    }

    private boolean isValidDirectory(final String directoryName) {
        if (directoryName == null || directoryName.trim().equals("")) {
            return false;
        }
        return true;
    }

    public List<MafiaTestResult> getMafiaTestResults(final List<String> pageNames) throws IOException {
        final List<MafiaTestResult> testResultRecords = new ArrayList<MafiaTestResult>();
        final TestHistory history = new TestHistory();
        final File historyDirectory = new File(reporterPluginConfig.getTestResultsDirectory() + File.separatorChar
                + "xml");
        for (final String pageName : pageNames) {
            history.readPageHistoryDirectory(historyDirectory, pageName);
            final PageHistory pageHistory = history.getPageHistory(pageName);
            final File htmlResultFile = new File(reporterPluginConfig.getTestResultsDirectory() + File.separatorChar
                    + "html" + File.separatorChar + pageName + ".html");
            final String htmlResult = FileUtils.fileRead(htmlResultFile);
            final String bodyHtmlResult = getBodyHtmlResult(htmlResult, pageName);
            testResultRecords.add(new MafiaTestResult(pageName, pageHistory.get(pageHistory.getLatestDate()),
                    bodyHtmlResult));
        }
        return testResultRecords;
    }

    private String getBodyHtmlResult(final String htmlResult, final String pageName) {
        final String header = "<h2>" + pageName + "</h2>";
        final int start = htmlResult.indexOf(header) + header.length();
        final int stop = htmlResult.indexOf("</body>");
        return htmlResult.substring(start, stop);
    }
}
