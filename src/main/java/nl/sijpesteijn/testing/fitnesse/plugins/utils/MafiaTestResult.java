package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import fitnesse.responders.run.TestSummary;
import fitnesse.wiki.PageType;

public class MafiaTestResult {

	private final String pageName;
	private final TestSummary testSummary;
	private final String htmlResult;
	private final PageType pageType;

	public MafiaTestResult(final PageType pageType, final String pageName, final TestSummary testSummary,
			final String htmlResult) {
		this.pageType = pageType;
		this.pageName = pageName;
		this.testSummary = testSummary;
		this.htmlResult = htmlResult;
	}

	public String getPageName() {
		return pageName;
	}

	public TestSummary getTestSummary() {
		return testSummary;
	}

	public String getHtmlResult() {
		return htmlResult;
	}

	public PageType getPageType() {
		return pageType;
	}
}
