package nl.sijpesteijn.testing.fitnesse.plugins.managers;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                testResultRecords.addAll(getMafiaTestResults(suite, PageType.SUITE, null, true));
            }
        }
    }

    private void addSuiteFilteredResults(final List<MafiaTestResult> testResultRecords) throws Exception {
        final String suitePageName = reporterPluginConfig.getSuitePageName();
        if (suitePageName != null && !suitePageName.equals("")) {
            testResultRecords.addAll(getMafiaTestResults(suitePageName, PageType.SUITE, null, true));
        }
    }

    private void addTestResults(final List<MafiaTestResult> testResultRecords) throws Exception {
        final List<String> tests = reporterPluginConfig.getTests();
        if (tests != null && !tests.isEmpty()) {
            for (final String test : tests) {
                testResultRecords.addAll(getMafiaTestResults(test, PageType.TEST, null, true));
            }
        }
    }

    private List<MafiaTestResult> getMafiaTestResults(final String pageName, final PageType pageType,
                                                      final String timestamp, final boolean addToOverview)
            throws MojoFailureException
    {
        final List<MafiaTestResult> testResults = new ArrayList<MafiaTestResult>();
        final TestHistory history = new TestHistory();
        history.readPageHistoryDirectory(historyDirectory, pageName);
        final PageHistory pageHistory = history.getPageHistory(pageName);
        if (pageHistory == null) {
            throw new MojoFailureException(
                "No test history for "
                        + pageName
                        + ". Did you use the FitNesseRunnerMojo to run the tests? FitNesse Report mojo is looking for test history in "
                        + historyDirectory);
        }
        Date selectedDate;
        if (timestamp == null || timestamp.equals("")) {
            selectedDate = pageHistory.getLatestDate();
        } else {
            selectedDate = new Date(Long.valueOf(timestamp));
        }
        final TestResultRecord testResultRecord = pageHistory.get(selectedDate);
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
            report.setDate(selectedDate);
            html = generateTestExecutionHTML((TestExecutionReport) report);
            testResults.add(new MafiaTestResult(pageType, pageName, testResultRecord, html, addToOverview));
        } else if (report instanceof SuiteExecutionReport) {
            html = generateSuiteExecutionHTML((SuiteExecutionReport) report);
            testResults.add(new MafiaTestResult(pageType, pageName, testResultRecord, html, addToOverview));
            final Map<String, String> testPages = getTestPages(html);
            for (final String testPageName : testPages.keySet()) {
                testResults
                    .addAll(getMafiaTestResults(testPageName, PageType.TEST, testPages.get(testPageName), false));
            }
        }
        return testResults;
    }

    private Map<String, String> getTestPages(final String suiteHtml) {
        final String regex = "<a href=\".*resultDate=.*\">";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(suiteHtml);
        final Map<String, String> testPages = new HashMap<String, String>();
        while (matcher.find()) {
            final String pageName = suiteHtml.substring(matcher.end(), suiteHtml.indexOf("</a>", matcher.start()));
            final int timestampStart = suiteHtml.indexOf("resultDate=", matcher.start()) + "resultDate=".length();
            final String timestamp = suiteHtml.substring(timestampStart, suiteHtml.indexOf(">", matcher.start()) - 1);
            testPages.put(pageName, timestamp);
        }
        return testPages;
    }

    private String removeSetupLinks(String html) {
        final String regex = "<a href=\".*SetUp.*\">.*</a>";
        final Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            final int start = html.indexOf("</a> <a href=", matcher.start()) + 4;
            html = html.substring(0, start) + html.substring(matcher.end(), html.length());
            matcher = pattern.matcher(html);
        }
        return html;
    }

    private String removeEditLinks(String html) {
        final String regex = "<a href=\".*?edit&amp;redirectToReferer=true&amp;redirectAction=\">\\(edit\\)</a>";
        final Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            final int start = html.indexOf("</a> <a href=", matcher.start()) + 4;
            html = html.substring(0, start) + html.substring(matcher.end(), html.length());
            matcher = pattern.matcher(html);
        }
        return html;
    }

    private String removeBodyTags(String html) {
        html = html.replaceAll("<body>", "");
        html = html.replaceAll("</body>", "");
        return html;
    }

    private String removeHtmlTags(String html) {
        html = html.replaceAll("<html>", "");
        html = html.replaceAll("</html>", "");
        return html;
    }

    private String removeHeadSections(String html) {
        int start = html.indexOf("<head>");
        int stop = html.indexOf("</head>");
        while (start > 0 && stop > 0) {
            html = html.substring(0, start) + html.substring(stop + "</head>".length(), html.length());
            start = html.indexOf("<head>");
            stop = html.indexOf("</head>");
        }
        return html;
    }

    private String formatImageLocations(final String html) {
        return html.replaceAll("/files/images/", "images/");
    }

    private String generateSuiteExecutionHTML(final SuiteExecutionReport report) throws MojoFailureException {
        velocityContext.put("suiteExecutionReport", report);
        final Template template = getTemplate("suiteExecutionReport.vm");
        String html = makeHTMLFromTemplate(template);
        html = removeHtmlTags(html);
        html = removeBodyTags(html);
        html = removeHeadSections(html);
        // html = removeSetupLinks(html);
        return html;
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
        String html = makeHTMLFromTemplate(template);
        html = formatImageLocations(html);
        html = removeHtmlTags(html);
        html = removeBodyTags(html);
        html = removeHeadSections(html);
        html = removeEditLinks(html);
        return html;
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
