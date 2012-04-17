package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import nl.sijpesteijn.testing.fitnesse.plugins.managers.ReportResource;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.reporting.MavenReportException;

import fitnesse.responders.run.TestSummary;

public class MafiaReportGenerator {

    private static final String PLUGIN_RESOURCES = "nl/sijpesteijn/testing/fitnesse/plugins/resources";
    private final Sink sink;
    private final ResourceBundle bundle;
    private final String outputDirectory;
    private final List<MafiaTestResult> mafiaTestResults;

    public MafiaReportGenerator(final Sink sink, final ResourceBundle bundle, final String outputDirectory,
                                final List<MafiaTestResult> mafiaTestResults)
    {
        this.sink = sink;
        this.bundle = bundle;
        this.outputDirectory = outputDirectory;
        this.mafiaTestResults = mafiaTestResults;
    }

    public void generate() throws MavenReportException {
        addStaticResources();

        createHead();
        createBody();

        flushAndCloseSink();
    }

    private void createHead() {
        sink.head();
        sink.title();
        sink.text(bundle.getString("report.mafia.title"));
        sink.title_();
        sink.head_();

    }

    private void createBody() {
        sink.body();
        addJavaScript();
        addStyles();
        createIntroduction();
        createSummary();
        createFileList();
        createDetails();

        sink.body_();
    }

    private void addStyles() {
        final String stylesheet = SpecialPages.stylesheet;
        sink.rawText("<style type=\"text/css\">" + stylesheet + "</style>");
    }

    private void addJavaScript() {
        final String javascript = SpecialPages.javascript;
        sink.rawText("<script type='text/javascript'>" + javascript + "</script>");
    }

    private void flushAndCloseSink() {
        sink.flush();
        // sink.close();
    }

    private void createIntroduction() {
        sink.section1();
        sink.sectionTitle1();
        sink.text(bundle.getString("report.mafia.title"));
        sink.sectionTitle1_();

        sink.paragraph();
        sink.text(bundle.getString("report.mafia.moreInfo") + " ");
        sink.link(bundle.getString("report.mafia.moreInfo.link"));
        sink.text(bundle.getString("report.mafia.moreInfo.linkName"));
        sink.link_();
        sink.text(".");

        sink.paragraph_();
        sink.section1_();
    }

    private void createSummary() {
        final TestSummary testSummary = getTestSummary();
        sink.section1();
        sink.sectionTitle1();
        sink.text(bundle.getString("report.mafia.summary"));
        sink.sectionTitle1_();

        sink.table();

        sink.tableRow();
        sink.tableCell();
        sink.text(bundle.getString("report.mafia.success"));
        sink.tableCell_();
        sink.tableCell();
        sink.text("" + testSummary.getRight());
        sink.nonBreakingSpace();
        iconSuccess();
        sink.tableCell_();
        sink.tableRow_();

        sink.tableRow();
        sink.tableCell();
        sink.text(bundle.getString("report.mafia.error"));
        sink.tableCell_();
        sink.tableCell();
        sink.text("" + testSummary.getWrong());
        sink.nonBreakingSpace();
        iconError();
        sink.tableCell_();
        sink.tableRow_();

        sink.tableRow();
        sink.tableCell();
        sink.text(bundle.getString("report.mafia.exception"));
        sink.tableCell_();
        sink.tableCell();
        sink.text("" + testSummary.getExceptions());
        sink.nonBreakingSpace();
        iconException();
        sink.tableCell_();
        sink.tableRow_();

        sink.tableRow();
        sink.tableCell();
        sink.text(bundle.getString("report.mafia.ignore"));
        sink.tableCell_();
        sink.tableCell();
        sink.text("" + testSummary.getIgnores());
        sink.nonBreakingSpace();
        iconIgnore();
        sink.tableCell_();
        sink.tableRow_();

        sink.table_();
        sink.section1_();
    }

    private TestSummary getTestSummary() {
        final TestSummary testSummary = new TestSummary();
        for (final MafiaTestResult mafiaTestResult : mafiaTestResults) {
            testSummary.exceptions += mafiaTestResult.getTestSummary().exceptions;
            testSummary.ignores += mafiaTestResult.getTestSummary().ignores;
            testSummary.right += mafiaTestResult.getTestSummary().right;
            testSummary.wrong += mafiaTestResult.getTestSummary().wrong;
        }
        return testSummary;
    }

    private void createFileList() {
        sink.section1();
        sink.sectionTitle1();
        sink.text(bundle.getString("report.mafia.resultList.summary"));
        sink.sectionTitle1_();

        sink.table();
        sink.tableRow();
        sink.tableHeaderCell();
        sink.text(bundle.getString("report.mafia.resultList.summary"));
        sink.tableHeaderCell_();
        sink.tableHeaderCell();
        sink.text(bundle.getString("report.mafia.success").substring(0, 1));
        sink.nonBreakingSpace();
        iconSuccess();
        sink.tableHeaderCell_();
        sink.tableHeaderCell();
        sink.text(bundle.getString("report.mafia.error").substring(0, 1));
        sink.nonBreakingSpace();
        iconError();
        sink.tableHeaderCell_();
        sink.tableHeaderCell();
        sink.text(bundle.getString("report.mafia.exception").substring(0, 1));
        sink.nonBreakingSpace();
        iconException();
        sink.tableHeaderCell_();
        sink.tableHeaderCell();
        sink.text(bundle.getString("report.mafia.ignore").substring(0, 1));
        sink.nonBreakingSpace();
        iconIgnore();
        sink.tableHeaderCell_();
        sink.tableRow_();

        for (final MafiaTestResult mafiaTestResult : mafiaTestResults) {

            sink.tableRow();
            sink.tableCell();
            sink.link("#" + mafiaTestResult.getPageName());
            sink.text(mafiaTestResult.getPageName());
            sink.link_();
            sink.tableCell_();
            sink.tableCell();
            sink.text("" + mafiaTestResult.getTestSummary().getRight());
            sink.tableCell_();
            sink.tableCell();
            sink.text("" + mafiaTestResult.getTestSummary().getWrong());
            sink.tableCell_();
            sink.tableCell();
            sink.text("" + mafiaTestResult.getTestSummary().getExceptions());
            sink.tableCell_();
            sink.tableCell();
            sink.text("" + mafiaTestResult.getTestSummary().getIgnores());
            sink.tableCell_();
            sink.tableRow_();
        }
        sink.table_();
        sink.section1_();
    }

    private void createDetails() {
        sink.section1();
        sink.sectionTitle1();
        sink.text(bundle.getString("report.mafia.resultDetails.summary"));
        sink.sectionTitle1_();

        for (final MafiaTestResult mafiaTestResult : mafiaTestResults) {
            sink.anchor(mafiaTestResult.getPageName());
            sink.table();
            sink.tableRow();
            sink.tableHeaderCell();
            sink.text(mafiaTestResult.getPageName());
            sink.tableHeaderCell_();
            sink.tableRow_();
            sink.tableRow();
            sink.tableCell();
            sink.rawText(mafiaTestResult.getHtmlResult());
            sink.tableCell_();
            sink.tableRow_();
            sink.table_();
            sink.anchor_();
        }
        sink.section1_();
    }

    private void iconIgnore() {
        sink.figure();
        sink.figureCaption();
        sink.text(bundle.getString("report.mafia.ignore.info"));
        sink.figureCaption_();
        sink.figureGraphics("images/icon_ignore_sml.gif");
        sink.figure_();
    }

    private void iconException() {
        sink.figure();
        sink.figureCaption();
        sink.text(bundle.getString("report.mafia.exception.info"));
        sink.figureCaption_();
        sink.figureGraphics("images/icon_exception_sml.gif");
        sink.figure_();
    }

    private void iconError() {
        sink.figure();
        sink.figureCaption();
        sink.text(bundle.getString("report.mafia.error.info"));
        sink.figureCaption_();
        sink.figureGraphics("images/icon_error_sml.gif");
        sink.figure_();
    }

    private void iconSuccess() {
        sink.figure();
        sink.figureCaption();
        sink.text(bundle.getString("report.mafia.success.info"));
        sink.figureCaption_();
        sink.figureGraphics("images/icon_success_sml.gif");
        sink.figure_();
    }

    private void addStaticResources() throws MavenReportException {

        final ReportResource rresource = new ReportResource(PLUGIN_RESOURCES, outputDirectory);
        createDirectoryIfNotExists(outputDirectory);
        createDirectoryIfNotExists(outputDirectory + "/images");
        try {
            rresource.copy("images/icon_error_sml.gif");
            rresource.copy("images/icon_exception_sml.gif");
            rresource.copy("images/icon_ignore_sml.gif");
            rresource.copy("images/icon_success_sml.gif");
            rresource.copy("images/collapsableClosed.gif");
            rresource.copy("images/collapsableOpen.gif");

        } catch (final IOException e) {
            throw new MavenReportException("Unable to copy static resources.", e);
        }
    }

    private void createDirectoryIfNotExists(final String directory) {
        final File output = new File(directory);
        if (!output.exists()) {
            output.mkdirs();
        }
    }
}
