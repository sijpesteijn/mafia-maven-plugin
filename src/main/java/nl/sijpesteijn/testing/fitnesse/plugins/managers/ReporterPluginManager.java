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
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.reporting.MavenReportException;

import fitnesse.wiki.PageType;

/**
 * Plugin manager responsible for collecting report results.
 * 
 */
public class ReporterPluginManager implements PluginManager {

    private final ReporterPluginConfig reporterPluginConfig;
    MafiaResultCollector collector;
    private final Log mavenLogger;

    /**
     * 
     * @param reporterPluginConfig
     *        {@link nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.ReporterPluginConfig}
     */
    public ReporterPluginManager(final ReporterPluginConfig reporterPluginConfig) {
        this.reporterPluginConfig = reporterPluginConfig;
        mavenLogger = reporterPluginConfig.getMavenLogger();
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
            final MafiaReportGenerator generator =
                    new MafiaReportGenerator(reporterPluginConfig.getSink(), reporterPluginConfig.getResourceBundle(),
                        reporterPluginConfig.getOutputDirectory(), getTestResults(), getSuiteResults(),
                        getSuiteFilteredResults());
            generator.generate();
        } catch (final MavenReportException e) {
            throw new MojoFailureException("Could not generate mafia report: ", e);
        } catch (final Exception e) {
            throw new MojoFailureException(e.getMessage());
        }
    }

    private List<MafiaTestResult> getSuiteResults() throws Exception {
        final List<MafiaTestResult> testResultRecords = new ArrayList<MafiaTestResult>();
        final List<String> suites = reporterPluginConfig.getSuites();
        if (suites != null && !suites.isEmpty()) {
            mavenLogger.info("Collecting suite reports...");
            for (final String suite : suites) {
                mavenLogger.info("Collecting test report for suite: " + suite);
                testResultRecords.add(collector.getMafiaTestResult(suite, PageType.SUITE, null, true));
            }
        }
        return testResultRecords;
    }

    private List<MafiaTestResult> getSuiteFilteredResults() throws Exception {
        final List<MafiaTestResult> testResultRecords = new ArrayList<MafiaTestResult>();
        final String suitePageName = reporterPluginConfig.getSuitePageName();
        if (suitePageName != null && !suitePageName.equals("")) {
            mavenLogger.info("Collecting report for suite with suitePageName=" + suitePageName + " ...");
            testResultRecords.add(collector.getMafiaTestResult(suitePageName, PageType.SUITE, null, true));
        }
        return testResultRecords;
    }

    private List<MafiaTestResult> getTestResults() throws Exception {
        final List<MafiaTestResult> testResultRecords = new ArrayList<MafiaTestResult>();
        final List<String> tests = reporterPluginConfig.getTests();
        if (tests != null && !tests.isEmpty()) {
            mavenLogger.info("Collecting test reports...");
            for (final String test : tests) {
                mavenLogger.info("Collecting test report for test: " + test);
                testResultRecords.add(collector.getMafiaTestResult(test, PageType.TEST, null, true));
            }
        }
        return testResultRecords;
    }
}
