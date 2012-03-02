package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import java.io.IOException;

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
	public void announceNumberTestsToRun(final int arg0) {
		// log.info("announceNumberTestsToRun");
	}

	@Override
	public void errorOccured() {
		// log.info("errorOccured");

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
	public void testSystemStarted(final TestSystem testSystem, final String testSystemName, final String testRunner) {
		// TODO Auto-generated method stub

	}

	@Override
	public void testOutputChunk(final String output) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void newTestStarted(final WikiPage test, final TimeMeasurement timeMeasurement) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void testComplete(final WikiPage test, final TestSummary testSummary, final TimeMeasurement timeMeasurement)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
