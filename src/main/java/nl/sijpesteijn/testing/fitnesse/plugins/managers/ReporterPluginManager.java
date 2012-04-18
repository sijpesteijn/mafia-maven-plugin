package nl.sijpesteijn.testing.fitnesse.plugins.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.ReporterPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaReportGenerator;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaResultCollector;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaTestResult;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.reporting.MavenReportException;

import fitnesse.wiki.PageType;

/**
 * Plugin manager responsible for collecting report results.
 * 
 */
public class ReporterPluginManager implements PluginManager {

    private final ReporterPluginConfig reporterPluginConfig;
    MafiaResultCollector collector;

    /**
     * 
     * @param reporterPluginConfig
     *            {@link nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.ReporterPluginConfig}
     */
    public ReporterPluginManager(final ReporterPluginConfig reporterPluginConfig) {
        this.reporterPluginConfig = reporterPluginConfig;
        collector = new MafiaResultCollector(new File(reporterPluginConfig.getMafiaTestResultsDirectory()));
    }

    /**
     * Collect the reports
     * 
     * @throws MavenReportException
     */
    @Override
    public void run() throws MojoFailureException, MojoExecutionException {
        try {
            final List<MafiaTestResult> mafiaTestResults = getMafiaTestResults();
            final MafiaReportGenerator generator = new MafiaReportGenerator(reporterPluginConfig.getSink(),
                    reporterPluginConfig.getResourceBundle(), reporterPluginConfig.getOutputDirectory(),
                    mafiaTestResults);
            generator.generate();
        } catch (final MavenReportException e) {
            throw new MojoFailureException("Could not generate mafia report: ", e);
        } catch (final Exception e) {
            throw new MojoFailureException(e.getMessage());
        }
    }

    public List<MafiaTestResult> getMafiaTestResults() throws Exception {
        final List<MafiaTestResult> testResultRecords = new ArrayList<MafiaTestResult>();
        addSuiteResults(testResultRecords);
        addSuiteFilteredResults(testResultRecords);
        addTestResults(testResultRecords);
        return testResultRecords;
    }

    private void addSuiteResults(final List<MafiaTestResult> testResultRecords) throws Exception {
        final List<String> suites = reporterPluginConfig.getSuites();
        if (suites != null && !suites.isEmpty()) {
            for (final String suite : suites) {
                testResultRecords.addAll(collector.getMafiaTestResults(suite, PageType.SUITE, null, true));
            }
        }
    }

    private void addSuiteFilteredResults(final List<MafiaTestResult> testResultRecords) throws Exception {
        final String suitePageName = reporterPluginConfig.getSuitePageName();
        if (suitePageName != null && !suitePageName.equals("")) {
            testResultRecords.addAll(collector.getMafiaTestResults(suitePageName, PageType.SUITE, null, true));
        }
    }

    private void addTestResults(final List<MafiaTestResult> testResultRecords) throws Exception {
        final List<String> tests = reporterPluginConfig.getTests();
        if (tests != null && !tests.isEmpty()) {
            for (final String test : tests) {
                testResultRecords.addAll(collector.getMafiaTestResults(test, PageType.TEST, null, true));
            }
        }
    }
}
