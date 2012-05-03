package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MafiaHtmlConverter {

    public String removeEditLinks(String html) {
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

    public String removeBodyTags(String html) {
        html = html.replaceAll("<body>", "");
        html = html.replaceAll("</body>", "");
        return html;
    }

    public String removeHtmlTags(String html) {
        html = html.replaceAll("<html>", "");
        html = html.replaceAll("</html>", "");
        return html;
    }

    public String removeHeadSections(String html) {
        int start = html.indexOf("<head>");
        int stop = html.indexOf("</head>");
        while (start > 0 && stop > 0) {
            html = html.substring(0, start) + html.substring(stop + "</head>".length(), html.length());
            start = html.indexOf("<head>");
            stop = html.indexOf("</head>");
        }
        return html;
    }

    public String formatImageLocations(final String html) {
        return html.replaceAll("/files/images/", "images/");
    }

    public String updateSuiteTestLinks(String html) {
        final String regex = "<a href=\"[^#].*pageHistory&resultDate=.*>";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(html);
        int offset = 9;
        while (matcher.find()) {
            html =
                    html.substring(0, matcher.start() + offset) + "#"
                            + html.substring(matcher.start() + offset++, html.length());
        }
        return html;
    }

    public String updateSubResultLinks(final String testPageName, final String html, final String subHtml) {
        final String url = getTestPageNameUrl(testPageName, html);
        final String regex = "<a name=.*\"";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(subHtml);
        if (matcher.find()) {
            return subHtml.substring(0, matcher.start() + 9) + url + "\""
                    + subHtml.substring(matcher.end(), subHtml.length());
        }
        return subHtml;
    }

    private String getTestPageNameUrl(final String testPageName, final String html) {
        final String regex = "<a href=.*" + testPageName.replace(".", ".*") + ".*resultDate=[0-9]*";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return html.substring(matcher.start() + 10, matcher.end());
        }
        return null;
    }

}
