package nl.sijpesteijn.testing.fitnesse.plugins.report;

import org.apache.tools.ant.util.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

/**
 * @author Gijs Sijpesteijn
 */
public class MafiaReportBuilder implements ReportBuilder {
    private Document document;
    private Element head;
    private Element body;

    public MafiaReportBuilder() {
        document = Jsoup.parse("<html><head/><body/></html>");
        head = document.head();
        body = document.body();
    }

    @Override
    public ReportBuilder withSummary(MafiaTestSummary testSummary, MafiaTestSummary assertionSummary) {
        Element div = body.appendElement("div");
        div.attr("id","test-summary");
        div.addClass(testSummary.successful() ? "pass" : "fail");
        addSummary(testSummary, div, "Test Pages:");
        div.appendText("     ");
        addSummary(testSummary, div, "Assertions:");
        div.appendText(" (" + getTime(assertionSummary.getTestTime()) + ")");
        return this;
    }

    private void addSummary(MafiaTestSummary testSummary, Element div, String title) {
        Element element = div.appendElement("strong");
        element.text(title);
        div.appendText(testSummary.getRight() + " right, " + testSummary.getWrong() + " wrong, " +
                testSummary.getIgnores() + " ignores, " + testSummary.getExceptions() + " exceptions");
    }

    @Override
    public ReportBuilder withStyleSheets(List<String> styleSheets) {
        for(String styleSheet: styleSheets) {
            Element element = head.appendElement("link");
            element.attr("rel", "stylesheet").attr("type", "text/css").attr("href", styleSheet);
        }
        return this;
    }

    @Override
    public ReportBuilder withTests(List<MafiaTestResult> mafiaTestResults) {
        Element div = body.appendElement("div");
        div.attr("id","test-summaries");
        div.appendElement("h2").text("Test Summaries");
        div.appendElement("h3").text("slim:fitnesse.slim.SlimService");
        Element ul = div.appendElement("ul");
        ul.attr("id","test-system-slim:fitnesse.slim.SlimService");
        for(MafiaTestResult result : mafiaTestResults) {
            Element li = ul.appendElement("li");
            Element span1 = li.appendElement("span");
            span1.addClass("results" + (result.getTestSummary().successful() ? " pass" : " fail"));
            span1.appendText(" " + result.getTestSummary().getRight() + " right, " + result.getTestSummary().getWrong() + " wrong, " +
                    result.getTestSummary().getIgnores() + " ignores, " + result.getTestSummary().getExceptions() + " exceptions ");
            Element a = li.appendElement("a");
            String[] parts = result.getTestSummary().getWikiPage().split("\\.");
            String name = parts[parts.length -1];
            a.attr("href", "?#" + name);
            a.addClass("link");
            a.text(name + (result.getTestSummary().getSuiteFilter() != null && !result.getTestSummary().getSuiteFilter().isEmpty()?"( " + result.getTestSummary().getSuiteFilter() + " )": ""));
            Element span2 = li.appendElement("span");
            span2.text(" (" + getTime(result.getTestSummary().getTestTime()) + ")");

        }
        div.appendElement("h3").text("slim:fitnesse.slim.SlimService");
        Element h2 = body.appendElement("h2");
        h2.text("Test output");
        for(MafiaTestResult result : mafiaTestResults) {
            String[] parts = result.getTestSummary().getWikiPage().split("\\.");
            String name = parts[parts.length -1];

            Element resultBody = Jsoup.parse(result.getHtmlResult()).body();
            Element article = resultBody.select("article").get(0);
            Element articleDiv = article.select("div.test_output_name").get(0).clone();
            Element h3 = articleDiv.select("h3 > a").get(0);
            h3.attr("id", name);
            h3.attr("href", name);
            h3.text(name + (result.getTestSummary().getSuiteFilter() != null && !result.getTestSummary().getSuiteFilter().isEmpty()?"( " + result.getTestSummary().getSuiteFilter() + " )": ""));
            article.children().select("div.alternating_block").first().after(articleDiv);

            article.select("script").remove();
            article.select("div#test-summary").remove();
            article.select("div#test-action").remove();
            Element bodyArticle = body.appendElement("article");
            bodyArticle.html(article.html());
        }
        return this;
    }

    @Override
    public String getHtmlAsString() {
        return document.html();
    }

    @Override
    public ReportBuilder withJavaScriptFiles(List<String> javascripts) {
        for(String javascript: javascripts) {
            Element element = head.appendElement("script");
            element.attr("type","text/javascript").attr("src",javascript);
        }
        return this;
    }

    /**
     * Format a test duration.
     *
     * @param testTime - number of milliseconds.
     * @return - xhxmxs, x = number.
     */
    private final String getTime(final long testTime) {
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

}
