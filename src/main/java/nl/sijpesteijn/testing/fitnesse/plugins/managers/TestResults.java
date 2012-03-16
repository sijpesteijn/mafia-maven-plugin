package nl.sijpesteijn.testing.fitnesse.plugins.managers;

import java.util.ArrayList;
import java.util.List;

import nl.sijpesteijn.testing.fitnesse.plugins.executioners.TestSummaryAndDuration;

public class TestResults {

    private final TestSummaryAndDuration testSummaryAndDuration = new TestSummaryAndDuration();

    public TestSummaryAndDuration getTestSummaryAndDuration() {
        return testSummaryAndDuration;
    }

    public List<TestResult> getTestResults() {
        return new ArrayList<TestResult>();
    }

}
