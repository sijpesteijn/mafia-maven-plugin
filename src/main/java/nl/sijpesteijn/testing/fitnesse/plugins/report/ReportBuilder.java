package nl.sijpesteijn.testing.fitnesse.plugins.report;

import java.util.List;

/**
 * @author Gijs Sijpesteijn
 */
public interface ReportBuilder {

    ReportBuilder withSummary(MafiaTestSummary testSummary, MafiaTestSummary assertionSummary);

    ReportBuilder withStyleSheets(List<String> styleSheets);

    ReportBuilder withTests(List<MafiaTestResult> testSummaries);

    String getHtmlAsString();

    ReportBuilder withJavaScriptFiles(List<String> javascript);
}
