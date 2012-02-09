package nl.sijpesteijn.testing.fitnesse.plugins.executioners;

import fitnesse.responders.run.TestSummary;

/**
 * Sub class of {@link fitnesse.responders.run.TestSummary} with extra duration property
 * 
 */
public class TestSummaryAndDuration extends TestSummary {

    public long duration;

    public TestSummaryAndDuration(final int right, final int wrong, final int ignores, final int exceptions,
                                  final long duration)
    {
        super(right, wrong, ignores, exceptions);
        this.duration = duration;
    }

    public TestSummaryAndDuration() {
        duration = 0;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(final long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return super.toString() + "," + duration + " time";
    }
}
