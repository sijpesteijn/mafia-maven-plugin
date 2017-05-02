package nl.sijpesteijn.testing.fitnesse.plugins.runner;

import static junit.framework.Assert.*;
import nl.sijpesteijn.testing.fitnesse.plugins.AbstractFitNesseTest;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaException;

import org.junit.Before;
import org.junit.Test;

/**
 * User: gijs
 * Date: 5/10/13 5:51 PM
 */
public class FitNesseCommanderTest extends AbstractFitNesseTest {
    private static FitNesseCommander commander;

    @Before
    public void setup() {
        commander = new FitNesseCommander(getFitnesseCommanderConfig());
    }

    @Test
    public void start() throws Throwable {
        try {
            commander.stop();
            commander.start();
            assertFalse(commander.hasError());
            assertTrue(commander.getOutput().contains("FitNesse (v20121220) Started...") || commander
                    .getErrorOutput()
                    .contains("Unpacking"));
            assertNotNull(commander.getErrorOutput());
        } finally {
            commander.stop();
        }
    }

    @Test
    public void startNoJvmArguments() throws Throwable {
        try {
            commander.stop();

            FitNesseCommanderConfig config = new FitNesseCommanderConfig(PORT, WIKI_ROOT, NAME_ROOT_PAGE,
                    LOG_DIRECTORY, 0, FITNESSE_JAR_PATH,
                    null, mockLog, null, null, null, null, 4,null);
            commander = new FitNesseCommander(config);
            commander.start();
            assertFalse(commander.hasError());
        } finally {
            try {
                commander.stop();
            } catch (MafiaException me) {

            }
        }
    }

    @Test(expected = MafiaException.class)
    public void failureWikiRoot() throws Exception {
        FitNesseCommanderConfig config = new FitNesseCommanderConfig(PORT, "$&#&^&$", NAME_ROOT_PAGE,
                null, 0, FITNESSE_JAR_PATH,
                null, mockLog, null, null, null, null, 4,null);
        commander = new FitNesseCommander(config);
        commander.start();
    }

    @Test
    public void checkErrorLog() throws Exception {
        try {
            FitNesseCommander commander = new FitNesseCommander(getFitnesseCommanderConfig());
            commander.start();
            commander.start();
            assertTrue(commander.hasError());
        } finally {
            commander.stop();
        }
    }
}
