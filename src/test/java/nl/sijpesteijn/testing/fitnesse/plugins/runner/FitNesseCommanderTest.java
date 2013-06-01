package nl.sijpesteijn.testing.fitnesse.plugins.runner;

import nl.sijpesteijn.testing.fitnesse.plugins.AbstractFitNesseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: gijs
 * Date: 5/10/13 5:51 PM
 */
public class FitNesseCommanderTest extends AbstractFitNesseTest {
    private FitNesseCommander commander;

    @Before
    public void setup() {
        commander = new FitNesseCommander(getFitnesseCommanderConfig());
    }

    @After
    public void tearDown() throws Throwable {
        commander.stop();
    }

    @Test
    public void start() throws Throwable {
        commander.stop();
        commander.start();
        assertFalse(commander.hasError());
        assertTrue(commander.getOutput().contains("FitNesse (v20121220) Started...") || commander.getErrorOutput().equals("Unpacking new version of FitNesse resources. Please be patient."));
        assertNotNull(commander.getErrorOutput());
    }

    @Test
    public void startNoJvmArguments() throws Throwable {
        FitNesseCommanderConfig config = new FitNesseCommanderConfig(PORT, WIKI_ROOT, NAME_ROOT_PAGE, LOG_DIRECTORY,0,
                FITNESSE_JAR_PATH, null, mockLog);
        commander = new FitNesseCommander(config);
        commander.stop();
        commander.start();
        assertFalse(commander.hasError());
    }

}
