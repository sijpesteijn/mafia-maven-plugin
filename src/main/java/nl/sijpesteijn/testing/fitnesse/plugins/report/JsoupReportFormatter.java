package nl.sijpesteijn.testing.fitnesse.plugins.report;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Iterator;

/**
 * JsoupReportFormatter.
 */
public class JsoupReportFormatter implements ReportFormatter {

    /**
     *
     * {@inheritDoc}
     */
    public final String getHtml(final MafiaTestResult testResult, final TestType testType) {
        long timeStamp = System.currentTimeMillis();
        Document document = Jsoup.parse(testResult.getHtmlResult());
        Element body = document.body();
        Element header = body.select("header").get(0);
        String title = header.select("h1 > a").get(0).text();
        header.tagName("h1");
        header.text(title);

        // remove elements by id
        removeElementById(body, "error-nav");

        // remove elements by css
        removeElementByCss(body, "h2", "footer", "a.top_of_page", "nav");

        updateHXtoH1(body, timeStamp);
        updateIncludes(body);

        updateSummary(body, testType, testResult, timeStamp);
        return body.toString();
    }

    /**
     * Update the test summary.
     *
     * @param body       - body element.
     * @param testType   - test type. TEST/SUITE/FILTERED_SUITE.
     * @param testResult - test result.
     * @param timeStamp  - the timestamp.
     */
    private void updateSummary(final Element body, final TestType testType, final MafiaTestResult testResult,
                               final long timeStamp) {
        Elements scripts = body.select("script");
        String result = getTestResult(scripts);

        Element summary = body.getElementById("test-summary");
        summary.html(result);
        if (testType != TestType.TEST) {
            String summaryList = "<ul id=\"test-system-slim\">";
            summaryList += getSummaryList(scripts, timeStamp);
            summaryList += "</ul>";
            Element testSummaries = body.getElementById("test-summaries");
            testSummaries.html(summaryList);
        }
        summary.removeAttr("class");
        summary.attr("class", getStyle(testResult.getTestSummary()));

        scripts.remove();

    }

    /**
     * Get the summary list from the script elements.
     *
     * @param scripts   - the script elements.
     * @param timeStamp - the timestamp.
     * @return - result string.
     */
    @SuppressWarnings("PMD")
    private String getSummaryList(final Elements scripts, final long timeStamp) {
        String startStr = "existingContent + \"<li>";
        StringBuffer list = new StringBuffer();
        Iterator<Element> iterator = scripts.iterator();
        while (iterator.hasNext()) {
            String script = iterator.next().html();
            int start = script.indexOf(startStr);
            if (start > 0) {
                script = "<li>" + script.substring(start + startStr.length(), script.length() - 2);
                script = script.replaceAll("\\\\n", "");
                script = script.replaceAll("\\\\t", "");
                script = script.replaceAll("\\\\", "");
                int anchorStart = script.indexOf("#");
                int anchorEnd = script.indexOf("\"", anchorStart);
                String anchor = "#" + timeStamp + script.substring(anchorStart + 1, anchorEnd - 1);
                list.append(script.substring(0, anchorStart) + anchor + script.substring(anchorEnd, script.length()));
            }
        }

        return list.toString();
    }

    /**
     * Get the test result from the script elements.
     *
     * @param scripts - script elements.
     * @return - script string.
     */
    private String getTestResult(final Elements scripts) {
        Iterator<Element> iterator = scripts.iterator();
        String result;
        while (iterator.hasNext()) {
            Element script = iterator.next();
            result = extractTestResult(script.html());
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * Check the test summary to determine the style.
     *
     * @param testSummary - mafia test summary.
     * @return - style string.
     */
    private String getStyle(final MafiaTestSummary testSummary) {
        String style = "pass";
        if (testSummary.getWrong() > 0) {
            style = "fail";
        } else if (testSummary.getExceptions() > 0 || testSummary.getIgnores() > 0) {
            style = "error";
        }
        return style;
    }

    /**
     * Extract the test result from the html.
     *
     * @param htmlResult - html
     * @return - extracted test result.
     */
    private String extractTestResult(final String htmlResult) {
        int start = htmlResult.indexOf("getElementById(\"test-summary\").innerHTML = \"<strong");
        if (start > 0) {
            int end = htmlResult.indexOf("\";", start + "getElementById(\"test-summary\").innerHTML = \"".length());
            String testResult =
                    htmlResult.substring(start + "getElementById(\"test-summary\").innerHTML = \"".length(), end);
            start = testResult.indexOf("<strong>Assertions");
            if (start > 0) {
                return testResult.substring(start, testResult.length());
            }
            return testResult;
        }
        return null;
    }

    /**
     * Update the includes.
     *
     * @param body - body element.
     */
    private void updateIncludes(final Element body) {
        Elements includes = body.select("p.title");
        Iterator<Element> iterator = includes.iterator();
        while (iterator.hasNext()) {
            Element include = iterator.next();
            Elements edit = include.select("a.edit");
            if (edit != null && !edit.isEmpty()) {
                edit.remove();
            }
            Elements as = include.select("a");
            if (as != null && !as.isEmpty()) {
                include.text(as.get(0).text());
            }
        }
    }

    /**
     * Update all hx elements to h1.
     *
     * @param body      - body element.
     * @param timeStamp - the timestamp.
     */
    private void updateHXtoH1(final Element body, final long timeStamp) {
        Elements h3 = body.select("h3");
        Iterator<Element> iterator = h3.iterator();
        while (iterator.hasNext()) {
            Element hThree = iterator.next();
            String title = hThree.select("a").text();
            hThree.tagName("h1");
            hThree.html("<a name=\"" + timeStamp + title + "\">" + title + "</a>");
        }
        Elements h2 = body.select("h2");
        iterator = h2.iterator();
        while (iterator.hasNext()) {
            Element h2El = iterator.next();
            h2El.tagName("h1");
        }
    }

    /**
     * Remove elements by css.
     *
     * @param body - body element.
     * @param css  - css search string.
     */
    private void removeElementByCss(final Element body, final String... css) {
        for (String cs : css) {
            Elements elements = body.select(cs);
            if (elements != null) {
                elements.remove();
            }
        }

    }

    /**
     * Remove elements by id.
     *
     * @param body - body element.
     * @param ids  - ids to remove.
     */
    private void removeElementById(final Element body, final String... ids) {
        for (String id : ids) {
            Element elementById = body.getElementById(id);
            if (elementById != null) {
                elementById.remove();
            }
        }
    }
}
