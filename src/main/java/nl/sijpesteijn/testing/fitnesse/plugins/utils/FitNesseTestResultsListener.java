package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import org.apache.maven.plugin.logging.Log;

import util.TimeMeasurement;
import fitnesse.responders.run.CompositeExecutionLog;
import fitnesse.responders.run.ResultsListener;
import fitnesse.responders.run.TestSummary;
import fitnesse.responders.run.TestSystem;
import fitnesse.wiki.WikiPage;

/**
 * TODO do something with this information.
 */
public class FitNesseTestResultsListener implements ResultsListener {

    private final Log log;

    public FitNesseTestResultsListener(final Log log) {
        this.log = log;
    }

    @Override
    public void allTestingComplete(final TimeMeasurement arg0) throws Exception {
        // log.info("allTestingComplete");
    }

    @Override
    public void announceNumberTestsToRun(final int arg0) {
        // log.info("announceNumberTestsToRun");
    }

    @Override
    public void errorOccured() {
        // log.info("errorOccured");

    }

    @Override
    public void newTestStarted(final WikiPage arg0, final TimeMeasurement arg1) throws Exception {
        // log.info("newTestStarted");

    }

    @Override
    public void setExecutionLogAndTrackingId(final String arg0, final CompositeExecutionLog arg1) throws Exception {
        // log.info("setExecutionLogAndTrackingId");

    }

    @Override
    public void testComplete(final WikiPage arg0, final TestSummary arg1, final TimeMeasurement arg2) throws Exception {
        // log.info("testComplete");

    }

    @Override
    public void testOutputChunk(final String arg0) throws Exception {
        // log.info("testOutputChunk");

    }

    @Override
    public void testSystemStarted(final TestSystem arg0, final String arg1, final String arg2) throws Exception {
        // log.info("testSystemStarted");

    }

}
