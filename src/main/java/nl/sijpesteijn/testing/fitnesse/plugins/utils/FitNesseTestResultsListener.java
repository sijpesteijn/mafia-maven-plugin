package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import java.io.File;
import java.io.IOException;

import util.TimeMeasurement;
import fitnesse.responders.run.CompositeExecutionLog;
import fitnesse.responders.run.ResultsListener;
import fitnesse.responders.run.TestExecutionReport;
import fitnesse.responders.run.TestPage;
import fitnesse.responders.run.TestSummary;
import fitnesse.responders.run.TestSystem;

/**
 * TODO do something with this information.
 */
public class FitNesseTestResultsListener implements ResultsListener {

    private final File outputDirectory;
    private final boolean testPagesOnly;

    public FitNesseTestResultsListener(final File outputDirectory, final boolean testPagesOnly) {
        this.outputDirectory = outputDirectory;
        this.testPagesOnly = testPagesOnly;
    }

    @Override
    public void allTestingComplete(final TimeMeasurement totalTimeMeasurement) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setExecutionLogAndTrackingId(final String stopResponderId, final CompositeExecutionLog log) {
        // TODO Auto-generated method stub

    }

    @Override
    public void announceNumberTestsToRun(final int testsToRun) {
        // TODO Auto-generated method stub

    }

    @Override
    public void testSystemStarted(final TestSystem testSystem, final String testSystemName, final String testRunner) {
        // TODO Auto-generated method stub

    }

    @Override
    public void newTestStarted(final TestPage test, final TimeMeasurement timeMeasurement) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void testOutputChunk(final String output) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void testComplete(final TestPage test, final TestSummary testSummary, final TimeMeasurement timeMeasurement)
            throws IOException {
        if (isTestPage(test)) {
            generateTestReport(test, testSummary, timeMeasurement);
        }
        if (isSuitePage(test))
            generateSuiteReport(test, testSummary, timeMeasurement);
    }

    private void generateSuiteReport(final TestPage test, final TestSummary testSummary,
            final TimeMeasurement timeMeasurement) {
        // TODO Auto-generated method stub

    }

    private boolean isSuitePage(final TestPage test) {
        // TODO Auto-generated method stub
        return false;
    }

    private void generateTestReport(final TestPage test, final TestSummary testSummary,
            final TimeMeasurement timeMeasurement) {
        // TODO Auto-generated method stub
        final TestExecutionReport testExecutionReport = new TestExecutionReport();
    }

    private boolean isTestPage(final TestPage test) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void errorOccured() {
        // TODO Auto-generated method stub

    }

}
