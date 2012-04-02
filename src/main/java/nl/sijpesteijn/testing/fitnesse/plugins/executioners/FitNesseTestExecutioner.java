package nl.sijpesteijn.testing.fitnesse.plugins.executioners;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;

import fitnesse.Arguments;
import fitnesse.ComponentFactory;
import fitnesse.FitNesseContext;
import fitnesse.WikiPageFactory;
import fitnesse.responders.run.JavaFormatter;
import fitnesse.responders.run.SuiteXmlFormatter;
import fitnesse.responders.run.TestSummary;
import fitnesse.responders.run.formatters.XmlFormatter;
import fitnesse.wiki.PageType;
import fitnesse.wiki.WikiPage;
import fitnesseMain.FitNesseMain;

/**
 * This class is responsible for running FitNesse tests
 * 
 */
public class FitNesseTestExecutioner {
	private final String fitNesseRootPath;
	private final String outputPath;
	private final int fitnessePort;
	private final String nameRootPage;

	/**
	 * 
	 * @param fitNesseRootPath
	 *            {@link java.lang.String}
	 * @param outputPath
	 *            {@link java.lang.String}
	 * @param fitnessePort
	 *            {@link int}
	 * @throws Exception
	 */
	public FitNesseTestExecutioner(final String fitNesseRootPath, final String nameRootPage, final String outputPath,
			final int fitnessePort) throws Exception {
		this.fitNesseRootPath = fitNesseRootPath;
		this.nameRootPage = nameRootPage;
		this.outputPath = outputPath;
		this.fitnessePort = fitnessePort;
	}

	/**
	 * Run a single FitNesse test
	 * 
	 * @param testName
	 *            {@link java.lang.String}
	 * @return {@link fitnesse.responders.run.TestSummary}
	 * @throws MojoFailureException
	 */
	public TestSummary runTest(final String testName) throws MojoFailureException {
		return run(testName, PageType.TEST, null);
	}

	/**
	 * Run a suite of FitNesse tests
	 * 
	 * @param suiteName
	 *            {@link java.lang.String}
	 * @return {@link fitnesse.responders.run.TestSummary}
	 * @throws MojoFailureException
	 */
	public TestSummary runTestSuite(final String suiteName) throws MojoFailureException {
		return run(suiteName, PageType.SUITE, null);
	}

	/**
	 * Run FitNesse tests by tag filter
	 * 
	 * @param suiteFilter
	 *            {@link java.lang.String}
	 * @param suitePageName
	 *            {@link java.lang.String}
	 * @return {@link fitnesse.responders.run.TestSummary}
	 * @throws MojoFailureException
	 */
	public TestSummary runByTagFilter(final String suiteFilter, final String suitePageName) throws MojoFailureException {
		return run(suitePageName, PageType.SUITE, suiteFilter);
	}

	/**
	 * Method responsible for the actual test run.
	 * 
	 * @param testName
	 *            {@link java.lang.String}
	 * @param pageType
	 *            {@link java.lang.String}
	 * @param suiteFilter
	 *            {@link java.lang.String}
	 * @return {@link fitnesse.responders.run.TestSummary}
	 * @throws MojoFailureException
	 */
	private TestSummary run(final String testName, final PageType pageType, final String suiteFilter)
			throws MojoFailureException {
		final JavaFormatter testFormatter = createTestFormatter(testName, pageType);
		final Arguments arguments = new Arguments();
		arguments.setDaysTillVersionsExpire("0");
		arguments.setInstallOnly(false);
		arguments.setOmitUpdates(true);
		arguments.setPort(String.valueOf(fitnessePort));
		arguments.setRootPath(fitNesseRootPath);
		arguments.setRootDirectory(nameRootPage);
		// arguments.setCommand(getTestUrl(testName,
		// pageType.toString().toLowerCase(), suiteFilter));
		FitNesseMain.dontExitAfterSingleCommand = true;
		try {
			FitNesseMain.launchFitNesse(arguments);
		} catch (final Exception exception) {
			throw new MojoFailureException(exception.getMessage());
		}
		return testFormatter.getTotalSummary();
	}

	private JavaFormatter createTestFormatter(final String testName, final PageType pageType)
			throws MojoFailureException {
		final JavaFormatter testFormatter = JavaFormatter.getInstance(testName);
		try {
			testFormatter.setResultsRepository(new JavaFormatter.FolderResultsRepository(outputPath + "/html/",
					fitNesseRootPath));
			testFormatter.setListener(createXmlFormatter(testName, pageType));
		} catch (final Exception e) {
			throw new MojoFailureException(e.getMessage());
		}

		return testFormatter;
	}

	/**
	 * Construct the url to pass to FitNesse
	 * 
	 * @param pageName
	 *            {@link java.lang.String}
	 * @param pageType
	 *            {@link java.lang.String}
	 * @param suiteFilter
	 *            {@link java.lang.String}
	 * @return {@link java.lang.String}
	 */
	private String getTestUrl(final String pageName, final String pageType, final String suiteFilter) {
		if (suiteFilter != null)
			return pageName + "?responder=suite&suiteFilter=" + suiteFilter;
		else
			return pageName + "?" + pageType + "&nohistory=true&format=java";
	}

	private XmlFormatter createXmlFormatter(final String testName, final PageType pageType) throws Exception {
		final ComponentFactory componentFactory = new ComponentFactory();
		final WikiPageFactory wikiPageFactory = new WikiPageFactory();
		final WikiPage page = wikiPageFactory.makeRootPage(fitNesseRootPath, testName, componentFactory);
		final XmlFormatter.WriterFactory writerSource = new XmlFormatter.WriterFactory() {
			@Override
			public Writer getWriter(final FitNesseContext context, final WikiPage page, final TestSummary counts,
					final long time) throws IOException {
				final String resultDirectory = outputPath + File.separatorChar + "xml" + File.separatorChar
						+ page.getName() + File.separatorChar;
				FileUtils.mkdir(resultDirectory);
				return new FileWriter(new File(resultDirectory + "20120312210436_1_0_0_0.xml"));
			}
		};

		final FitNesseContext context = new FitNesseContext();
		context.rootPath = fitNesseRootPath;
		if (pageType == PageType.TEST) {
			return new XmlFormatter(context, page, writerSource);
		} else {
			return new SuiteXmlFormatter(context, page, writerSource);
		}
	}

}
