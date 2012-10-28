package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import java.io.File;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import util.FileUtil;
import fitnesse.FitNesseContext;
import fitnesse.responders.run.ExecutionReport;
import fitnesse.responders.run.SuiteExecutionReport;
import fitnesse.responders.run.TestExecutionReport;
import fitnesse.responders.testHistory.PageHistory;
import fitnesse.responders.testHistory.TestHistory;
import fitnesse.responders.testHistory.TestResultRecord;
import fitnesse.wiki.PageType;

public class MafiaResultCollector {

	private final SimpleDateFormat dateFormat = new SimpleDateFormat(TestHistory.TEST_RESULT_FILE_DATE_PATTERN);
	private final File historyDirectory;
	private VelocityContext velocityContext;
	MafiaHtmlConverter converter = new MafiaHtmlConverter();

	public MafiaResultCollector(final File historyDirectory) {
		this.historyDirectory = historyDirectory;
	}

	public MafiaTestResult getMafiaTestResult(final String pageName, final PageType pageType, final String timestamp,
			final boolean addToOverview) throws MojoFailureException {
		MafiaTestResult mafiaTestResult = null;
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
		final Date selectedDate = getSelectedDate(pageName, timestamp, pageHistory);
		final TestResultRecord testResultRecord = pageHistory.get(selectedDate);
		if (testResultRecord == null) {
			throw new MojoFailureException("Exception: Could not load test result record: " + pageName
					+ ", timestamp: " + timestamp);
		}
		velocityContext = new VelocityContext();

		String content;
		try {
			content = FileUtil.getFileContent(testResultRecord.getFile());
		} catch (final Exception e) {
			throw new MojoFailureException("Exception: Could not load test result record content: " + pageName
					+ ", timestamp: " + timestamp, e);
		}
		ExecutionReport report;
		try {
			report = ExecutionReport.makeReport(content);
		} catch (final Exception e) {
			throw new MojoFailureException("Exception: Could not create execution report: " + pageName
					+ ", timestamp: " + timestamp, e);
		}
		String html = "";
		if (report instanceof TestExecutionReport) {
			report.setDate(selectedDate);
			html = generateTestExecutionHTML((TestExecutionReport) report);
			mafiaTestResult = new MafiaTestResult(pageType, pageName, testResultRecord, html, addToOverview);
		} else if (report instanceof SuiteExecutionReport) {
			html = generateSuiteExecutionHTML((SuiteExecutionReport) report);
			mafiaTestResult = new MafiaTestResult(pageType, pageName, testResultRecord, html, addToOverview);
			final Map<String, String> testPages = getTestPages(html);
			for (final String testPageName : testPages.keySet()) {
				final MafiaTestResult subMafiaTestResult = getMafiaTestResult(testPageName, PageType.TEST,
						testPages.get(testPageName), false);
				final String subHtml = subMafiaTestResult.getHtmlResult();
				final String updatedSubHtml = converter.updateSubResultLinks(testPageName, html, subHtml);
				subMafiaTestResult.setHtmlResult(updatedSubHtml);
				mafiaTestResult.addSubResult(subMafiaTestResult);
			}
		}
		return mafiaTestResult;
	}

	private Date getSelectedDate(final String pageName, final String timestamp, final PageHistory pageHistory)
			throws MojoFailureException {
		if (timestamp == null || timestamp.equals("")) {
			return pageHistory.getLatestDate();
		} else {
			try {
				return dateFormat.parse(timestamp);
			} catch (final ParseException e) {
				throw new MojoFailureException("Exception: Could not parse timestamp for test result record: "
						+ pageName + ", timestamp: " + timestamp + ", dateformat: "
						+ TestHistory.TEST_RESULT_FILE_DATE_PATTERN);
			}
		}
	}

	private Map<String, String> getTestPages(final String suiteHtml) {
		final String regex = "<a href=\".*resultDate=.*\">";
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(suiteHtml);
		final Map<String, String> testPages = new LinkedHashMap<String, String>();
		while (matcher.find()) {
			final String pageName = suiteHtml.substring(matcher.end(), suiteHtml.indexOf("</a>", matcher.start()));
			final int timestampStart = suiteHtml.indexOf("resultDate=", matcher.start()) + "resultDate=".length();
			final String timestamp = suiteHtml.substring(timestampStart, suiteHtml.indexOf(">", matcher.start()) - 1);
			testPages.put(pageName, timestamp);
		}
		return testPages;
	}

	private String generateSuiteExecutionHTML(final SuiteExecutionReport report) throws MojoFailureException {
		velocityContext.put("suiteExecutionReport", report);
		final Template template = getTemplate("suiteExecutionReport.vm");
		String html = makeHTMLFromTemplate(template);
		html = converter.removeHtmlTags(html);
		html = converter.removeBodyTags(html);
		html = converter.removeHeadSections(html);
		html = converter.updateSuiteTestLinks(html);
		return html;
	}

	private Template getTemplate(final String templateName) throws MojoFailureException {
		final Template template;
		try {
			template = new FitNesseContext().pageFactory.getVelocityEngine().getTemplate(templateName);
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
		html = converter.formatImageLocations(html);
		html = converter.removeHtmlTags(html);
		html = converter.removeBodyTags(html);
		html = converter.removeHeadSections(html);
		html = converter.removeEditLinks(html);
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
		}
	}

}
