package nl.sijpesteijn.testing.fitnesse.plugins.managers;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.ReporterPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaReportGenerator;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaTestResult;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.reporting.MavenReportException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import util.FileUtil;
import fitnesse.VelocityFactory;
import fitnesse.responders.run.ExecutionReport;
import fitnesse.responders.run.SuiteExecutionReport;
import fitnesse.responders.run.TestExecutionReport;
import fitnesse.responders.testHistory.PageHistory;
import fitnesse.responders.testHistory.TestHistory;
import fitnesse.responders.testHistory.TestResultRecord;
import fitnesse.wiki.PageType;

/**
 * Plugin manager responsible for collecting report results.
 * 
 */
public class ReporterPluginManager implements PluginManager {

    private final ReporterPluginConfig reporterPluginConfig;
    private VelocityContext velocityContext;
    File historyDirectory;

    /**
     * 
     * @param reporterPluginConfig
     *        {@link nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.ReporterPluginConfig}
     */
    public ReporterPluginManager(final ReporterPluginConfig reporterPluginConfig) {
        this.reporterPluginConfig = reporterPluginConfig;
        this.historyDirectory = new File(reporterPluginConfig.getMafiaTestResultsDirectory());
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
            final MafiaReportGenerator generator =
                    new MafiaReportGenerator(reporterPluginConfig.getSink(), reporterPluginConfig.getResourceBundle(),
                        reporterPluginConfig.getOutputDirectory(), mafiaTestResults);
            generator.generate();
        } catch (final MavenReportException e) {
            new MojoFailureException("Could not generate mafia report: ", e);
        } catch (final Exception e) {
            new MojoFailureException("Could not generate mafia report: ", e);
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
                testResultRecords.add(getMafiaTestResult(suite, PageType.SUITE));
            }
        }
    }

    private void addSuiteFilteredResults(final List<MafiaTestResult> testResultRecords) throws Exception {
        final String suitePageName = reporterPluginConfig.getSuitePageName();
        if (suitePageName != null && !suitePageName.equals("")) {
            testResultRecords.add(getMafiaTestResult(suitePageName, PageType.SUITE));
        }
    }

    private void addTestResults(final List<MafiaTestResult> testResultRecords) throws Exception {
        final List<String> tests = reporterPluginConfig.getTests();
        if (tests != null && !tests.isEmpty()) {
            for (final String test : tests) {
                testResultRecords.add(getMafiaTestResult(test, PageType.TEST));
            }
        }
    }

    private MafiaTestResult getMafiaTestResult(final String pageName, final PageType pageType)
            throws MojoFailureException
    {
        final TestHistory history = new TestHistory();
        history.readPageHistoryDirectory(historyDirectory, pageName);
        final PageHistory pageHistory = history.getPageHistory(pageName);
        final Date latestDate = pageHistory.getLatestDate();
        final TestResultRecord testResultRecord = pageHistory.get(latestDate);
        velocityContext = new VelocityContext();

        String content;
        try {
            content = FileUtil.getFileContent(testResultRecord.getFile());
        } catch (final Exception e) {
            throw new MojoFailureException("Exception: Could not load test result record: " + pageName, e);
        }
        ExecutionReport report;
        try {
            report = ExecutionReport.makeReport(content);
        } catch (final Exception e) {
            throw new MojoFailureException("Exception: Could not create execution report: " + pageName, e);
        }
        String html = "";
        if (report instanceof TestExecutionReport) {
            report.setDate(latestDate);
            html = generateTestExecutionHTML((TestExecutionReport) report);
        } else if (report instanceof SuiteExecutionReport) {
            html = generateSuiteExecutionHTML((SuiteExecutionReport) report);
        }
        System.out.println(pageName + "*****************" + html);
        return new MafiaTestResult(pageType, pageName, testResultRecord, html);
    }

    private String generateSuiteExecutionHTML(final SuiteExecutionReport report) throws MojoFailureException {
        velocityContext.put("suiteExecutionReport", report);
        final Template template = getTemplate("suiteExecutionReport.vm");
        return makeHTMLFromTemplate(template);
    }

    private Template getTemplate(final String templateName) throws MojoFailureException {
        final Template template;
        try {
            template = VelocityFactory.getVelocityEngine().getTemplate(templateName);
        } catch (final ResourceNotFoundException e) {
            throw new MojoFailureException("ResourceNotFoundException: Could not load template: " + templateName);
        } catch (final ParseErrorException e) {
            throw new MojoFailureException("ParseErrorException: Could not parse template: " + templateName);
        } catch (final Exception e) {
            throw new MojoFailureException("Exception: Could not load template: " + templateName);
        }
        return template;
    }

    private String generateTestExecutionHTML(final TestExecutionReport report) throws MojoFailureException {
        velocityContext.put("testExecutionReport", report);
        final Template template = getTemplate("testExecutionReport.vm");
        return makeHTMLFromTemplate(template);
    }

    private String makeHTMLFromTemplate(final Template template) throws MojoFailureException {
        final StringWriter writer = new StringWriter();
        mergeTemplate(template, writer);
        return writer.toString();
    }

    private void mergeTemplate(final Template template, final StringWriter writer) throws MojoFailureException {
        try {
            template.merge(velocityContext, writer);
        } catch (final ResourceNotFoundException e) {
            throw new MojoFailureException("ResourceNotFoundException: Could not merge template: " + template.getName());
        } catch (final ParseErrorException e) {
            throw new MojoFailureException("ParseErrorException: Could not parse template: " + template.getName());
        } catch (final MethodInvocationException e) {
            throw new MojoFailureException("MethodInvocationException: Could not merge template: " + template.getName());
        } catch (final IOException e) {
            throw new MojoFailureException("IOException: Could not merge template: " + template.getName());
        }
    }
}
