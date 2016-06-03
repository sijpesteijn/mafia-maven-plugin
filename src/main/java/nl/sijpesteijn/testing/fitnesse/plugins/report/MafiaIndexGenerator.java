package nl.sijpesteijn.testing.fitnesse.plugins.report;

import org.apache.maven.doxia.sink.Sink;
import org.apache.tools.ant.util.DateUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

/**
 * MafiaIndexGenerator.
 */
public class MafiaIndexGenerator {

    /**
     * Test result repository.
     */
    private final TestResultRepository repository;

    /**
     * Resource bundle.
     */
    private final ResourceBundle bundle;

    /**
     * The sink.
     */
    private final Sink sink;

    /**
     * Mafia test summary.
     */
    private final MafiaTestSummary summary;

    /**
     * Report formatter.
     */
    private final ReportFormatter reportFormatter;

    /**
     * Zero.
     */
    private static final int ZERO = 0;

    /**
     * Constructor.
     *
     * @param repository      - the test result repository.
     * @param bundle          - the resource bundle.
     * @param sink            - the sink to write to.
     * @param summary         - the mafia test summary.
     * @param reportFormatter - the report formatter
     */
    public MafiaIndexGenerator(final TestResultRepository repository, final ResourceBundle bundle, final Sink sink,
                               final MafiaTestSummary summary, final ReportFormatter reportFormatter) {
        this.repository = repository;
        this.bundle = bundle;
        this.sink = sink;
        this.summary = summary;
        this.reportFormatter = reportFormatter;
    }

    /**
     * Start generating the index file.
     */
    public final void generate() {
        sink.head();
        sink.title();
        sink.text(bundle.getString("report.mafia.title"));
        sink.title_();
        sink.head_();

        addBody();
        flushAndCloseSink();
    }

    /**
     * Add the body element.
     */
    private void addBody() {
        sink.body();
        sink.rawText("<link rel=\"stylesheet\" type=\"text/css\" href=\"./css/fitnesse_wiki.css\">\n"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"./css/fitnesse_pages.css\">\n"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"./css/fitnesse_straight.css\">\n"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"./wysiwyg/wysiwyg.css\" media=\"screen\">\n"
            + "<link rel=\"wysiwyg.base\" href=\"/\">\n"
            + "<link rel=\"wysiwyg.stylesheet\" type=\"text/css\" href=\"./css/fitnesse.css\" \"media=\"screen\">\n"
            + "<link rel=\"wysiwyg.stylesheet\" type=\"text/css\" href=\"./wysiwyg/editor.css\" \"media=\"screen\">\n"
            + "<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"./images/favicon.ico\">\n"
            + "<script src=\"./javascript/jquery-1.7.2.min.js\" type=\"text/javascript\"></script>\n"
            + "<script src=\"./javascript/fitnesse.js\" type=\"text/javascript\"></script>\n"
            + "<script src=\"./wysiwyg/wysiwyg.js\" type=\"text/javascript\"></script>\n"
            + "<script src=\"./javascript/fitnesse_straight.js\" type=\"text/javascript\"></script>\n");

        addIntroduction();
        addTestSummary();
        sink.lineBreak();
        addTestsDetails(TestType.TEST, repository.getTestResults());
        addTestsDetails(TestType.SUITE, repository.getSuitesResults());
        addTestsDetails(TestType.FILTERED_SUITE, repository.getFilteredSuitesResults());
        addFooter();
        sink.body_();
    }

    /**
     * Add the introduction.
     */
    private void addIntroduction() {
        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        sink.section1();
        sink.sectionTitle1();
        sink.text(bundle.getString("report.mafia.title"));
        sink.sectionTitle1_();

        sink.lineBreak();
        sink.text("Run date: " + format.format(summary.getRunDate()));
        sink.lineBreak();
        sink.text("Total run duration: " + getTime(summary.getTestTime()));
        sink.lineBreak();
        sink.text("Test result summary: ");
        addRight(summary.getRight());
        addWrong(summary.getWrong());
        addException(summary.getExceptions());
        addIgnore(summary.getIgnores());

        sink.section1_();
    }

    /**
     * Add the test summary section.
     */
    private void addTestSummary() {
        sink.rawText("<div style=\"display:block;border: 1px solid #000;padding:5px\">");
        addSummary(TestType.TEST, repository.getTestResults());
        addSummary(TestType.SUITE, repository.getSuitesResults());
        addSummary(TestType.FILTERED_SUITE, repository.getFilteredSuitesResults());
        sink.rawText("</div>");
    }

    /**
     * Add the tests details.
     *
     * @param testType    - the test type.
     * @param testResults - the test results.
     */
    private void addTestsDetails(final TestType testType, final List<MafiaTestResult> testResults) {
        if (!testResults.isEmpty()) {
            sink.rawText("<div style=\"display:block;border: 1px solid #000;padding:5px\">");
            sink.text(testType.getSectionTitle());
            sink.lineBreak();

            for (MafiaTestResult testResult : testResults) {
                String html = reportFormatter.getHtml(testResult, testType);
                sink.rawText("<div style=\"display:block;padding:3px;background-color:#F8F8F8;\">");
                sink.rawText("<a name=\"" + testType.getType() + testResult.getName() + "\"></a>" + html);
                sink.rawText("</div>");
                sink.lineBreak();
            }
            sink.rawText("</div>");
        }
        sink.lineBreak();
    }

    /**
     * Add the summary for a test type.
     *
     * @param testType    - the test type.
     * @param testResults - the test results.
     */
    private void addSummary(final TestType testType, final List<MafiaTestResult> testResults) {
        if (!testResults.isEmpty()) {
            sink.text(testType.getSectionTitle());
            sink.lineBreak();
            sink.rawText("<div id=\"test-summaries\"><ul id=\"test-system-slim\">");
            for (MafiaTestResult testResult : testResults) {
                sink.rawText("<li>");
                sink.rawText(getSummary(testType, testResult.getName(), testResult.getTestSummary()));
                sink.rawText("</li>");
            }
            sink.rawText("</ul></div>");
        }

    }

    /**
     * Add the summary.
     *
     * @param testType    - the test type.
     * @param name        - the test name.
     * @param testSummary - the test summary.
     * @return - summary string.
     */
    private String getSummary(final TestType testType, final String name, final MafiaTestSummary testSummary) {
        String summary = "<span class=\"results " + getStyle(testSummary) + "\">" + testSummary.getRight()
                + " right, " + testSummary.getWrong() + " wrong, " + testSummary.getIgnores() + " ignore, "
                + testSummary.getExceptions() + " exceptions</span>";
        summary += "<a href=\"#" + testType.getType() + name + "\" class=\"link\">" + name + "</a> ";
        summary += "<span class>" + getTime(testSummary.getTestTime()) + "</span>";
        return summary;
    }

    /**
     * Add the footer element.
     */
    private void addFooter() {
        sink.text(bundle.getString("report.mafia.moreInfo") + " ");
        sink.link(bundle.getString("report.mafia.moreInfo.link"));
        sink.text(bundle.getString("report.mafia.moreInfo.linkName"));
        sink.link_();
        sink.rawText(".");
    }

    /**
     * Flush and close the sink.
     */
    private void flushAndCloseSink() {
        sink.flush();
    }

    /**
     * Add an ignore item.
     *
     * @param ignores - the number of ignores.
     */
    private void addIgnore(final int ignores) {
        sink.text(ignores + " ");
        iconIgnore();
        sink.text(" ");
    }

    /**
     * Add an exception item.
     *
     * @param exceptions - the number of exceptions.
     */
    private void addException(final int exceptions) {
        sink.text(exceptions + " ");
        iconException();
        sink.text(" ");
    }

    /**
     * Add a wrong item.
     *
     * @param wrong - the number of wrongs.
     */
    private void addWrong(final int wrong) {
        sink.text(wrong + " ");
        iconError();
        sink.text(" ");
    }

    /**
     * Add a right item.
     *
     * @param right - the number of rights.
     */
    private void addRight(final int right) {
        sink.text(right + " ");
        iconSuccess();
        sink.text(" ");
    }

    /**
     * Add ignore icon.
     */
    private void iconIgnore() {
        sink.figure();
        sink.figureCaption();
        sink.text(bundle.getString("report.mafia.ignore.info"));
        sink.figureCaption_();
        sink.figureGraphics("images/icon_info_sml.gif");
        sink.figure_();
    }

    /**
     * Add exception icon.
     */
    private void iconException() {
        sink.figure();
        sink.figureCaption();
        sink.text(bundle.getString("report.mafia.exception.info"));
        sink.figureCaption_();
        sink.figureGraphics("images/icon_warning_sml.gif");
        sink.figure_();
    }

    /**
     * Add error icon.
     */
    private void iconError() {
        sink.figure();
        sink.figureCaption();
        sink.text(bundle.getString("report.mafia.error.info"));
        sink.figureCaption_();
        sink.figureGraphics("images/icon_error_sml.gif");
        sink.figure_();
    }

    /**
     * Add success icon.
     */
    private void iconSuccess() {
        sink.figure();
        sink.figureCaption();
        sink.text(bundle.getString("report.mafia.success.info"));
        sink.figureCaption_();
        sink.figureGraphics("images/icon_success_sml.gif");
        sink.figure_();
    }

    /**
     * Format a test duration.
     *
     * @param testTime - number of milliseconds.
     * @return - xhxmxs, x = number.
     */
    public final String getTime(final long testTime) {
        float tt = (float) testTime / 1000;
        DecimalFormat df = new DecimalFormat("#");
        df.setRoundingMode(RoundingMode.UP);
        long rounded = Long.valueOf(df.format(tt) + "000");
        String hours = DateUtils.format(rounded, "H");
        String minutes = DateUtils.format(rounded, "m");
        String seconds = DateUtils.format(rounded, "s");
        String time = "";
        if (!hours.equals("0")) {
            time += hours + "h";
        }
        if (!minutes.equals("0")) {
            time += minutes + "m";
        }
        time += seconds + "s";
        if (time.equals("0s")) {
            return "<1s";
        }
        return time;
    }

    /**
     * Get the style based on the test result.
     *
     * @param testSummary - the test summary.
     * @return - style class.
     */
    private String getStyle(final MafiaTestSummary testSummary) {
        String style = "pass";
        if (testSummary.getWrong() > ZERO) {
            style = "fail";
        } else if (testSummary.getExceptions() > ZERO) {
            style = "error";
        }
        return style;
    }

}
