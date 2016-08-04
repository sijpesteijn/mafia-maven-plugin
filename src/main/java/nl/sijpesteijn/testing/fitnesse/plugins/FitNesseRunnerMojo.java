package nl.sijpesteijn.testing.fitnesse.plugins;

import fitnesse.wiki.PageType;
import nl.sijpesteijn.testing.fitnesse.plugins.report.*;
import nl.sijpesteijn.testing.fitnesse.plugins.runner.FitNesseCommander;
import nl.sijpesteijn.testing.fitnesse.plugins.runner.ResultStore;
import nl.sijpesteijn.testing.fitnesse.plugins.runner.TestCaller;
import nl.sijpesteijn.testing.fitnesse.plugins.runner.URLTestCaller;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.FitNesseResourceAccess;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaException;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaRuntimeException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

/**
 * Goal to run the Fitnesse tests.
 */
@Mojo(name = "test", defaultPhase = LifecyclePhase.INTEGRATION_TEST)
public class FitNesseRunnerMojo extends AbstractStartFitNesseMojo {
    private static final String PLUGIN_RESOURCES = "nl/sijpesteijn/testing/fitnesse/plugins/resources/";

    /**
     * Skip the running of test. Default false.
     */
    @Parameter(property = "mafia.test.skip", defaultValue = "false")
    private boolean skip;

    /**
     * Start a separate fitnesse instance for testing or test on a remote server. (Specified by protocol & host).
     */
    @Parameter(property = "startServer", defaultValue = "true")
    private boolean startServer;

    /**
     * The port number on which FitNesse is running the tests.
     */
    @Parameter(property = "fitNesseRunPort", defaultValue = "9091")
    private int fitNesseRunPort;


    /**
     * List of tests to be run.
     */
    @Parameter(property = "tests")
    private List<String> tests;

    /**
     * List of suites to be run.
     */
    @Parameter(property = "suites")
    private List<String> suites;

    /**
     * Name of the suite page name.
     */
    @Parameter(property = "suitePageName")
    private String suitePageName;

    /**
     * Suite filter to run in the specified suite (=suitePageName).
     */
    @Parameter(property = "suiteFilter")
    private String suiteFilter;

    /**
     * If true, the mojo will stop when it encountered an ignored error message.
     */
    @Parameter(property = "stopTestsOnIgnore", defaultValue = "false")
    private boolean stopTestsOnIgnore;

    /**
     * If true, the mojo will stop when it encountered an exception error message.
     */
    @Parameter(property = "stopTestsOnException", defaultValue = "true")
    private boolean stopTestsOnException;

    /**
     * If true, the mojo will stop when it encountered a wrong error message.
     */
    @Parameter(property = "stopTestsOnWrong", defaultValue = "true")
    private boolean stopTestsOnWrong;

    /**
     * If true, every test result will be written to the folder "surefire-reports" in the maven build directory. This
     * enabled tools like Jenkins to recognize if a test failed. Default: false.
     */
    @Parameter(property = "writeSurefireReports", defaultValue = "false")
    private boolean writeSurefireReports;

    @Parameter(defaultValue = "${project}")
    MavenProject mavenProject;

    /**
     * The result store that takes care of persisting the test results.
     */
    @Component(role = ResultStore.class)
    private ResultStore resultStore;

    @Component(role = ReportBuilder.class)
    private ReportBuilder reportBuilder;

    /**
     * Local commander to run tests on.
     */
    private FitNesseCommander commander;

    /**
     * {@inheritDoc}
     */
    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {
        getLog().debug(toString());
        if (!skip) {

            if (fitNesseRunPort == -1) {
                fitNesseRunPort = getAvailablePort();
            }

            if (startServer) {
                startCommander();
            }
            getLog().info("Starting test run....");
            try {
                Map<String, MafiaTestSummary> testSummaries = new HashMap<String, MafiaTestSummary>();
                String fitnesseFilesFolder = getWikiRoot() + File.separator + getNameRootPage() + File.separator + "files" + File.separator;
                String outputPath = fitnesseFilesFolder + File.separator + "mafiaResults" + File.separator;
                clearOutputDirectory(outputPath);
                File mafiaResultFolder = new File(outputPath);
                mafiaResultFolder.mkdirs();

                TestCaller testCaller = new URLTestCaller(fitNesseRunPort, "http", "localhost", new File(outputPath), resultStore);

                boolean keepgoing = true;
                if(keepgoing && tests != null) {
                    Iterator<String> iterator = tests.iterator();
                    while (keepgoing && iterator.hasNext()) {
                        String test = iterator.next();
                        MafiaTestSummary summary = testCaller.test(test, PageType.TEST, null, "/tests/");
                        testSummaries.put(test, summary);
                        keepgoing = checkResult(summary);
                    }
                }
                if (keepgoing && suites != null) {
                    Iterator<String> iterator = suites.iterator();
                    while(keepgoing && iterator.hasNext()) {
                        String suite = iterator.next();
                        MafiaTestSummary summary = testCaller.test(suite, PageType.SUITE, null, "/suites/");
                        testSummaries.put(suite, summary);
                        keepgoing = checkResult(summary);
                    }
                }
                if (keepgoing && !StringUtils.isEmpty(suitePageName) && !StringUtils.isEmpty(suiteFilter)) {
                    MafiaTestSummary summary = testCaller.test(suitePageName, PageType.SUITE, suiteFilter, "/filteredSuite/");
                    testSummaries.put(suitePageName + " (filter: " + suiteFilter + ")", summary);
                    keepgoing = checkResult(summary);
                }
                printTestResults(testSummaries);

                MafiaTestSummary testSummary = getTotalTestSummary(testSummaries);
                MafiaTestSummary assertionSummary = getTotalTestSummary(testSummaries);
                if (testSummary != null) {
                    resultStore.saveSummary(testSummary, new File(outputPath));
                }
                List<MafiaTestResult> testResults = new ArrayList<>();
                final MafiaTestResultRepository resultRepository =
                        new MafiaTestResultRepository(mafiaResultFolder, resultStore);
                testResults.addAll(resultRepository.getTestResults());
                testResults.addAll(resultRepository.getSuitesResults());
                testResults.addAll(resultRepository.getFilteredSuitesResults());

                List<String> styleSheets = new ArrayList<String>();
                styleSheets.add("css/fitnesse_wiki.css");
                styleSheets.add("css/fitnesse_pages.css");
                styleSheets.add("css/fitnesse_straight.css");
                styleSheets.add("css/fitnesse.css");
                List<String> javascriptFiles = new ArrayList<String>();
                javascriptFiles.add("javascript/jquery-1.7.2.min.js");
                javascriptFiles.add("javascript/fitnesse.js");
                javascriptFiles.add("javascript/fitnesse_straight.js");

                File customStyle = new File(fitnesseFilesFolder + File.separator + "fitnesse");
                File output = new File(outputPath + File.separator + "report");
                if(customStyle.exists()) {
                    try {
                        File css = new File(customStyle, "css");
                        if (css.exists()) {
                            for (File cssfile : css.listFiles()) {
                                styleSheets.add("css/" + cssfile.getName());
                            }
                        }

                        File javascript = new File(customStyle, "javascript");
                        if(javascript.exists()) {
                            for (File javascriptfile : javascript.listFiles()) {
                                javascriptFiles.add("javascript/" + javascriptfile.getName());
                            }
                        }
                        FileUtils.copyDirectory(customStyle, output);
                    } catch (IOException e) {
                        throw new MafiaException("Could not copy the custom report resources.");
                    }
                }

                try {
                    final ReportResource resource = new ReportResource(outputPath + File.separator + "report", PLUGIN_RESOURCES);
                    resource.copy("css/");
                    resource.copy("images/");
                    resource.copy("javascript/");
                } catch (IOException e) {
                    throw new MafiaException("Could not copy the report resources.");
                }

                String reportHtml = reportBuilder.withSummary(testSummary, assertionSummary)
                        .withStyleSheets(styleSheets)
                        .withJavaScriptFiles(javascriptFiles)
                        .withTests(testResults)
                        .getHtmlAsString();

                try {
                    FileUtils.writeStringToFile(new File(outputPath + "/report/fitnesse_report.html"), reportHtml);
                } catch (IOException e) {
                    throw new MafiaException("Could not create test report");
                }

                getLog().info("Finished test run.");
                if (!keepgoing) {
                    throw new MojoFailureException("Test run interrupted by stop conditions.");
                }
            } catch (MafiaException e) {
                throw new MojoFailureException("Failed to run tests. " + e.getMessage(), e);
            } finally {
                if (startServer) {
                    stopCommander();
                }
            }
        } else {
            getLog().info("Skipping mafia reporting.");
        }
    }

    private boolean checkResult(MafiaTestSummary summary) {
        if (summary.getWrong() > 0 && stopTestsOnWrong) {
            getLog().info(summary.getWikiPage() + " failed with wrong exception.");
            return false;
        }
        if (summary.getIgnores() > 0 && stopTestsOnIgnore) {
            getLog().info(summary.getWikiPage() + " failed with ignore exception.");
            return false;
        }
        if (summary.getExceptions() > 0 && stopTestsOnException) {
            getLog().info(summary.getWikiPage() + " failed with an exception.");
            return false;
        }
        return true;
    }


    private int getAvailablePort() {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(0);
            ss.setReuseAddress(true);
            return ss.getLocalPort();
        } catch (IOException e) {} finally {
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }
        return -1;
    }

    /**
     * Start the fitnesse commander.
     *
     * @throws MojoFailureException
     *             - unable to create FitNesseCommander.
     * @throws MojoExecutionException
     *             - unable to start commander.
     */
    private void startCommander() throws MojoFailureException, MojoExecutionException {
        commander =
                new FitNesseCommander(getCommanderConfig(getJvmDependencies(), getJvmArguments(), 0,
                        fitNesseRunPort, getFitNesseAuthenticateStart(), getConnectionAttempts()));
        try {
            commander.start();
        } catch (MafiaException me) {
            throw new MojoExecutionException(me.getMessage(), me);
        }
        getLog().debug("Fitnesse output: " + commander.getOutput());
        getLog().debug("Fitnesse error output: " + commander.getErrorOutput());
        if (commander.hasError()) {
            logErrorMessages(commander.getOutput(), commander.getErrorOutput());
            throw new MojoExecutionException("Could not start FitNesse");
        }
        getLog().info("FitNesse start on: http://localhost:" + fitNesseRunPort);
    }

    /**
     * Stop the fitnesse commander.
     *
     * @throws MojoExecutionException
     *             - unable to stop fitnesse.
     */
    private void stopCommander() throws MojoExecutionException {
        try {
            commander.stop();
            getLog().info("FitNesse stopped on: http://localhost:" + fitNesseRunPort);
        } catch (MafiaException me) {
            throw new MojoExecutionException(me.getMessage(), me);
        }
    }

    /**
     * Clear the test result output directory.
     *
     * @throws MojoFailureException
     *             - unable to delete output directory.
     * @param directory
     */
    private void clearOutputDirectory(String directory) throws MojoFailureException {
        try {
            FileUtils.deleteDirectory(new File(directory));
        } catch (IOException e) {
            throw new MojoFailureException("Could not clear output directory.", e);
        }
    }

    /**
     * Calculate the total test summary.
     *
     * @param testSummaries
     *            - the test summaries to save.
     * @throws MafiaException
     *             - unable to save test summaries.
     */
    private MafiaTestSummary getTotalTestSummary(final Map<String, MafiaTestSummary> testSummaries) throws MafiaException {
        if (testSummaries != null) {
            int exceptions = 0;
            int wrong = 0;
            int ignores = 0;
            int right = 0;
            long testTime = 0;
            for (Map.Entry<String, MafiaTestSummary> entry : testSummaries.entrySet()) {
                final MafiaTestSummary mafiaTestSummary = entry.getValue();
                exceptions += mafiaTestSummary.getExceptions();
                wrong += mafiaTestSummary.getWrong();
                ignores += mafiaTestSummary.getIgnores();
                right += mafiaTestSummary.getRight();
                testTime += mafiaTestSummary.getTestTime();
            }
            final MafiaTestSummary summary = new MafiaTestSummary(right, wrong, ignores, exceptions);
            summary.setTestTime(testTime);
            return summary;
        }
        return null;
    }

    /**
     * Write test results to maven log.
     *
     * @param testSummaries
     *            - test summaries to log.
     */
    private void printTestResults(final Map<String, MafiaTestSummary> testSummaries) {
        if (testSummaries != null) {
            for (Map.Entry<String, MafiaTestSummary> entry : testSummaries.entrySet()) {
                getLog().info("Test: " + entry.getKey() + "(" + entry.getValue().toString() + ")");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return super.toString()
                + ", FitNesseRunPort: " + fitNesseRunPort
                + ", Tests: " + tests
                + ", Suites: " + suites
                + ", Suite page name: " + suitePageName
                + ", Suite filter: " + suiteFilter
                + ", Stop tests on ignore: " + stopTestsOnIgnore
                + ", Stop tests on exception: " + stopTestsOnException
                + ", Stop tests on wrong: " + stopTestsOnWrong;
    }
}
