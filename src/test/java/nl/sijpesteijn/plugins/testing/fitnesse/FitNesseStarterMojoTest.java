package nl.sijpesteijn.plugins.testing.fitnesse;

import static org.junit.Assert.assertTrue;
import nl.sijpesteijn.testing.fitnesse.plugins.FitNesseStarterMojo;
import nl.sijpesteijn.testing.fitnesse.plugins.FitNesseStopperMojo;

import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * Test FitNesseStarterMojo
 */
public class FitNesseStarterMojoTest extends AbstractFitNesseTestCase {
    private FitNesseStarterMojo starterMojo;
    private FitNesseStopperMojo stopperMojo;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        starterMojo = configureStarterMojo();
        stopperMojo = configureStopperMojo();
    }

    @Test
    public void successfulStart() throws Exception {
        starterMojo.execute();
        stopperMojo.execute();
    }

    @Test
    public void failureStart() throws Exception {
        starterMojo.execute();
        try {
            starterMojo.execute();
        } catch (final MojoFailureException mfe) {
            final String message = mfe.getMessage();
            final String expected = "Could not start FitNesse: FitNesse cannot be started..."
                    + System.getProperty("line.separator") + "Port 9090 is already in use."
                    + System.getProperty("line.separator")
                    + "Use the -p <port#> command line argument to use a different port."
                    + System.getProperty("line.separator");
            assertTrue(expected.equals(message));
            stopperMojo.execute();
        }
    }

    public FitNesseStarterMojo getStarterMojo() {
        return starterMojo;
    }
}
