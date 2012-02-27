package nl.sijpesteijn.testing.fitnesse.plugins.executioners;

import java.io.IOException;

import nl.sijpesteijn.testing.fitnesse.plugins.utils.FitNesseTestResultsListener;

import org.apache.maven.plugin.MojoFailureException;

import fitnesse.Arguments;
import fitnesse.responders.run.JavaFormatter;
import fitnesse.responders.run.ResultsListener;
import fitnesse.responders.run.TestSummary;
import fitnesseMain.FitNesseMain;

/**
 * This class is responsible for running FitNesse tests
 * 
 */
public class FitNesseTestExecutioner {
	public static final String PAGE_TYPE_SUITE = "suite";
	public static final String PAGE_TYPE_TEST = "test";
	private final String fitNesseRootPath;
	private final String outputPath;
	private final int fitnessePort;
	private final ResultsListener resultsListener;
	private final String nameRootPage;

	/**
	 * 
	 * @param fitNesseRootPath
	 *            {@link java.lang.String}
	 * @param outputPath
	 *            {@link java.lang.String}
	 * @param fitnessePort
	 *            {@link int}
	 * @param resultsListener
	 *            {@link fitnesse.responders.run.ResultsListener}
	 */
	public FitNesseTestExecutioner(final String fitNesseRootPath, final String nameRootPage, final String outputPath,
			final int fitnessePort, final ResultsListener resultsListener) {
		this.fitNesseRootPath = fitNesseRootPath;
		this.nameRootPage = nameRootPage;
		this.outputPath = outputPath;
		this.fitnessePort = fitnessePort;
		this.resultsListener = resultsListener;
	}

	/**
	 * 
	 * @param fitNesseRootPath
	 *            {@link java.lang.String}
	 * @param outputPath
	 *            {@link java.lang.String}
	 * @param fitnessePort
	 *            {@link int}
	 */
	public FitNesseTestExecutioner(final String fitNesseRootPath, final String nameRootPage, final String outputPath,
			final int fitnessePort) {
		this(fitNesseRootPath, nameRootPage, outputPath, fitnessePort, new FitNesseTestResultsListener(null));
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
		return run(testName, PAGE_TYPE_TEST, null);
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
		return run(suiteName, PAGE_TYPE_SUITE, null);
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
		return run(suitePageName, PAGE_TYPE_SUITE, suiteFilter);
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
	private TestSummary run(final String testName, final String pageType, final String suiteFilter)
			throws MojoFailureException {
		final JavaFormatter testFormatter = JavaFormatter.getInstance(testName);
		try {
			final JavaFormatter.FolderResultsRepository mockResultsRepository = new JavaFormatter.FolderResultsRepository(
					outputPath, fitNesseRootPath);
			testFormatter.setResultsRepository(mockResultsRepository);
		} catch (final IOException ioException) {
			throw new MojoFailureException(ioException.getMessage());
		}
		testFormatter.setListener(this.resultsListener);
		final Arguments arguments = new Arguments();
		arguments.setDaysTillVersionsExpire("0");
		arguments.setInstallOnly(false);
		arguments.setOmitUpdates(true);
		arguments.setPort(String.valueOf(fitnessePort));
		arguments.setRootPath(fitNesseRootPath);
		arguments.setRootDirectory(nameRootPage);
		arguments.setCommand(getTestUrl(testName, pageType, suiteFilter));
		FitNesseMain.dontExitAfterSingleCommand = true;
		try {
			FitNesseMain.launchFitNesse(arguments);
		} catch (final Exception exception) {
			throw new MojoFailureException(exception.getMessage());
		}
		return testFormatter.getTotalSummary();
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
}
