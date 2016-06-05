package nl.sijpesteijn.testing.fitnesse.plugins.report;

import fitnesse.wiki.PageType;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Gijs Sijpesteijn
 */
public class MafiaReportBuilderTest {
    List<String> javascriptFiles = new ArrayList<>();
    List<String> styleSheets = new ArrayList<>();
    MafiaTestSummary totalSummary = new MafiaTestSummary(12, 3, 0, 5, 0, 160530112144l, "FrontPage", PageType.STATIC);
    MafiaTestSummary summary1 = new MafiaTestSummary(3, 3, 0, 5, 145689, 160530112144l, "FrontPage.Test1", PageType.TEST);
    MafiaTestSummary summary2 = new MafiaTestSummary(12, 0, 0, 0, 124560, 160530112144l, "FrontPage.Test2", PageType.TEST);
    MafiaTestSummary assertion = new MafiaTestSummary(132, 45, 0, 34, 3000549, 160530112144l, "FrontPage", PageType.STATIC);
    private List<MafiaTestResult> tests = new ArrayList<>();


    @Before
    public void setup() throws IOException {
        String html = FileUtils.readFileToString(new File("./src/test/resources/testresult.html"));
        styleSheets.add("/mafia/stylesheet_1.css");
        styleSheets.add("/mafia/stylesheet_2.css");
        javascriptFiles.add("/mafia/javascript_1.js");
        javascriptFiles.add("/mafia/javascript_2.js");
        MafiaTestResult result1 = new MafiaTestResult(new File(""), summary1, html);
        MafiaTestResult result2 = new MafiaTestResult(new File(""), summary2, html);
        tests.add(result1);
        tests.add(result2);
    }

    @Test
    public void testEmptyDocument() throws Exception {
        MafiaReportBuilder builder = new MafiaReportBuilder();
        String html = builder.getHtmlAsString();
        assertEquals("<html>\n <head></head>\n <body></body>\n</html>", html);
    }

    @Test
    public void testWithStyles() throws Exception {
        MafiaReportBuilder builder = new MafiaReportBuilder();
        String html = builder.withStyleSheets(styleSheets).getHtmlAsString();
        assertEquals("<html>\n" +
                " <head>\n" +
                "  <link rel=\"stylesheet\" type=\"text/css\" href=\"/mafia/stylesheet_1.css\" />\n" +
                "  <link rel=\"stylesheet\" type=\"text/css\" href=\"/mafia/stylesheet_2.css\" />\n" +
                " </head>\n" +
                " <body></body>\n" +
                "</html>", html);
    }

    @Test
    public void testWithJavascriptFiles() throws Exception {
        MafiaReportBuilder builder = new MafiaReportBuilder();
        String html = builder.withJavaScriptFiles(javascriptFiles).getHtmlAsString();
        assertEquals("<html>\n" +
                " <head>\n" +
                "  <script type=\"text/javascript\" src=\"/mafia/javascript_1.js\"></script>\n" +
                "  <script type=\"text/javascript\" src=\"/mafia/javascript_2.js\"></script>\n" +
                " </head>\n" +
                " <body></body>\n" +
                "</html>", html);
    }

    @Test
    public void testWithSummary() throws Exception {
        MafiaReportBuilder builder = new MafiaReportBuilder();
        String html = builder.withSummary(totalSummary, assertion).getHtmlAsString();
        assertEquals("<html>\n" +
                " <head></head>\n" +
                " <body>\n" +
                "  <div id=\"test-summary\" class=\" fail\">\n" +
                "   <strong>Test Pages:</strong>12 right, 3 wrong, 0 ignores, 5 exceptions \n" +
                "   <strong>Assertions:</strong>12 right, 3 wrong, 0 ignores, 5 exceptions (50m1s)\n" +
                "  </div>\n" +
                " </body>\n" +
                "</html>", html);
    }

    @Test
    public void testWithTests() throws Exception {
        MafiaReportBuilder builder = new MafiaReportBuilder();
        String html = builder.withTests(tests).getHtmlAsString();
        assertEquals(FileUtils.readFileToString(new File("./src/test/resources/fitnesse_report.html")), html);
    }
 }