package nl.sijpesteijn.plugins.testing.fitnesse;

import static org.junit.Assert.assertTrue;
import nl.sijpesteijn.testing.fitnesse.plugins.FitNesseStarterMojo;
import nl.sijpesteijn.testing.fitnesse.plugins.FitNesseStopperMojo;

import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Test;

/**
 * Test FitNesseStopperMojo.
 */
public class FitNesseStopperMojoTest extends AbstractFitNesseTestCase {
    private FitNesseStopperMojo stopperMojo;
    private FitNesseStarterMojo starterMojo;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        stopperMojo = configureStopperMojo();
        starterMojo = configureStarterMojo();
    }

    @Test
    public void succesfullStop() throws Exception {
        starterMojo.execute();
        stopperMojo.execute();
    }

    @Test
    public void failureStop() throws Exception {
        try {
            stopperMojo.execute();
        } catch (final MojoFailureException mfe) {
            final String message = mfe.getMessage();
            assertTrue(message
                    .equals("Could not stop FitNesse. It might not be running?"));
        }
    }

    public FitNesseStopperMojo getStopperMojo() {
        return stopperMojo;
    }

}
