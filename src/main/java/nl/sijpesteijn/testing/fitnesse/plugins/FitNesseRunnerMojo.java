package nl.sijpesteijn.testing.fitnesse.plugins;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import nl.sijpesteijn.testing.fitnesse.plugins.report.MafiaTestSummary;
import nl.sijpesteijn.testing.fitnesse.plugins.runner.FitNesseCommander;
import nl.sijpesteijn.testing.fitnesse.plugins.runner.FitNesseTestRunner;
import nl.sijpesteijn.testing.fitnesse.plugins.runner.ResultStore;
import nl.sijpesteijn.testing.fitnesse.plugins.runner.TestCaller;
import nl.sijpesteijn.testing.fitnesse.plugins.runner.URLTestCaller;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.FitNesseResourceAccess;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaException;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaRuntimeException;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.surefirereport.SurefireReportWriter;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.surefirereport.TestResult;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.surefirereport.TestResultReader;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Goal to run the Fitnesse tests.
 */
@Mojo(name = "test", defaultPhase = LifecyclePhase.INTEGRATION_TEST)
public class FitNesseRunnerMojo extends AbstractStartFitNesseMojo {

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
     * The directory where the Fitnesse reports have been generated.
     */
    @Parameter(property = "testResultsDirectory", defaultValue = "mafiaTestResults")
    private String testResultsDirectory;

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
     * The test result output directory.
     */
    private String outputDirectory;

    /**
     * The result store that takes care of persisting the test results.
     */
    @Component(role = ResultStore.class)
    private ResultStore resultStore;

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
                outputDirectory = getWikiRoot() + File.separator + getNameRootPage() + "/files/mafiaResults/";
                clearOutputDirectory(outputDirectory);
                new File(outputDirectory).mkdirs();

                final TestCaller testCaller = new URLTestCaller(fitNesseRunPort, "http", "localhost",
                    new File(outputDirectory), resultStore);

                final Date startDate = new Date();
                final FitNesseTestRunner runner = new FitNesseTestRunner(testCaller,
                    stopTestsOnIgnore, stopTestsOnException, stopTestsOnWrong, getLog());
                runner.runTests(tests);
                runner.runSuites(suites);
                runner.runFilteredSuite(suitePageName, suiteFilter);
                saveTestSummariesAndWriteProperties(runner.getTestSummaries(), startDate);
                printTestResults(runner.getTestSummaries());
                getLog().info("Finished test run.");
                copyFitNesseResourcesTo(outputDirectory
                    + FitNesseResourceAccess.RESOURCES_FOLDER_NAME_WITHIN_MAFIARESULTS);
                writeSurefireReportsIfNecessary();
            } catch (MafiaException e) {
                throw new MojoFailureException("Failed to run tests.", e);
            } finally {
                if (startServer) {
                    stopCommander();
                }
            }
        } else {
            getLog().info("Skipping mafia reporting.");
        }
    }

    private void copyFitNesseResourcesTo(String targetResourceDir) throws MojoFailureException {
        getLog().debug("Copying the fitnesse resources (css, js etc.) to " + targetResourceDir + " ...");
        FitNesseResourceAccess fitNesseJarAccess = new FitNesseResourceAccess(getMafiaProject());
        try {
            fitNesseJarAccess.copyResourcesTo(targetResourceDir);
        } catch (MafiaRuntimeException e) {
            getLog().warn("Couldn't copy resources (css, js) to " + targetResourceDir, e);
        }
    }

    private void writeSurefireReportsIfNecessary() {
        if (writeSurefireReports) {
            getLog().info("Writing content of testResults to surefire-reports...");

            String testResultsFolder = getWikiRoot() + File.separator + getNameRootPage() + "/files/testResults/";
            TestResultReader testResultReader = new TestResultReader(getLog());
            List<TestResult> allTestResultFiles = testResultReader.readAllTestResultFiles(new File(
                testResultsFolder));

            String buildDirectory = mavenProject.getBuild().getDirectory();
            File surefireReportBaseDir = new File(buildDirectory, "/surefire-reports/");
            surefireReportBaseDir.mkdirs();
            SurefireReportWriter surefireReportWriter = new SurefireReportWriter(getLog(), outputDirectory,
                testResultsFolder);
            surefireReportWriter.serialize(allTestResultFiles, surefireReportBaseDir);
        }
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
                fitNesseRunPort, getFitNesseAuthenticateStart()));
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
     * Save the test summaries and write properties file with total test results.
     *
     * @param testSummaries
     *            - the test summaries to save.
     * @param runDate
     *            - date the test was run.
     * @throws MafiaException
     *             - unable to save test summaries.
     */
    private void saveTestSummariesAndWriteProperties(final Map<String, MafiaTestSummary> testSummaries,
        final Date runDate) throws MafiaException {
        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
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
            final Properties properties = new Properties();
            properties.put("exceptions", "" + summary.getExceptions());
            properties.put("wrong", "" + summary.getWrong());
            properties.put("ignores", "" + summary.getIgnores());
            properties.put("right", "" + summary.getRight());
            properties.put("testTime", "" + summary.getTestTime());
            properties.put("runDate", format.format(runDate));
            saveProperties(properties);
        }
    }

    /**
     * Save the properties file with total test results to disk.
     *
     * @param properties
     *            - properties object.
     * @throws MafiaException
     *             - unable to save properties.
     */
    private void saveProperties(final Properties properties) throws MafiaException {
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(outputDirectory + File.separator + "mafiaresults.properties");
        } catch (FileNotFoundException e) {
            throw new MafiaException("Could not open mafiaresults.properties", e);
        }

        try {
            properties.store(outputStream, "Mafia test result properties");
            outputStream.flush();
        } catch (IOException e) {
            throw new MafiaException("Could not save mafia test results.", e);
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                throw new MafiaException("Could not close property file stream.", e);
            }
        }

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
            + ", Test results directory: " + testResultsDirectory
            + ", Tests: " + tests
            + ", Suites: " + suites
            + ", Suite page name: " + suitePageName
            + ", Suite filter: " + suiteFilter
            + ", Stop tests on ignore: " + stopTestsOnIgnore
            + ", Stop tests on exception: " + stopTestsOnException
            + ", Stop tests on wrong: " + stopTestsOnWrong;
    }
}
