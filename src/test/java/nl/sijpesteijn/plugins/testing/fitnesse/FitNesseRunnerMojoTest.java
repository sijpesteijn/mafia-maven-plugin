package nl.sijpesteijn.plugins.testing.fitnesse;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Map;

import nl.sijpesteijn.testing.fitnesse.plugins.FitNesseContentMojo;
import nl.sijpesteijn.testing.fitnesse.plugins.FitNesseRunnerMojo;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.FirstTimeWriter;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.FitNesseExtractor;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * Test only covers configuration test. Execution test is done in
 * {@link nl.sijpesteijn.plugins.testing.fitnesse.IntegrationTest}
 */
public class FitNesseRunnerMojoTest extends AbstractFitNesseTestCase {
    private FitNesseRunnerMojo runnerMojo;
    private FitNesseContentMojo contentMojo;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        runnerMojo = configureRunnerMojo();
        contentMojo = configureContentMojo();
    }

    @Ignore
    @Test
    @SuppressWarnings("rawtypes")
    public void testCheckArguments() throws Exception {
        final Map map = getVariablesAndValuesFromObject(runnerMojo);
        final int port = (Integer) map.get("port");
        assertTrue(port == 9091);
        final String wikiRoot = (String) map.get("wikiRoot");
        assertTrue(wikiRoot.replace("\\", "/").equals(getTestDirectory() + TARGET));
        final String nameRootPage = (String) map.get("nameRootPage");
        assertTrue(nameRootPage.equals(FITNESSE_ROOT));
        final String mafiaTestResultsDirectory = (String) map.get("mafiaTestResultsDirectory");
        assertTrue(mafiaTestResultsDirectory.equals("mafiaTestResults"));
        final String[] tests = (String[]) map.get("tests");
        assertTrue(tests[0].equals("FrontPage.BuyMilkSuite.BuyMilk"));
        assertTrue(tests[1].equals("FrontPage.BuyMilkSuite.BuyMilk1"));
        final String[] suites = (String[]) map.get("suites");
        assertTrue(suites[0].equals("FrontPage.BuyMilkSuite"));
        final Boolean stopTestsOnFailure = (Boolean) map.get("stopTestsOnFailure");
        assertTrue(stopTestsOnFailure);
        final Boolean stopTestsOnIgnore = (Boolean) map.get("stopTestsOnIgnore");
        assertTrue(stopTestsOnIgnore);
        final Boolean stopTestsOnException = (Boolean) map.get("stopTestsOnException");
        assertTrue(stopTestsOnException);
        final Boolean stopTestsOnWrong = (Boolean) map.get("stopTestsOnWrong");
        assertTrue(stopTestsOnWrong);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void runTests() throws Exception {
        deleteTestDirectory();
        FitNesseExtractor.extract(new SystemStreamLog(), getTestDirectory() + "/target/", REPO);

        contentMojo.execute();
        new FirstTimeWriter(getTestDirectory() + "/target/" + FITNESSE_ROOT);

        createDummySuite();
        createDummyTest("");
        createDummyTest("1");

        runnerMojo.execute();

        final File mafiaResultDirectory = new File(getTestDirectory() + "/target/" + FITNESSE_ROOT + "/files/"
                + MAFIA_TEST_RESULTS);
        assertTrue(mafiaResultDirectory.exists());
        final List files = FileUtils.getFiles(mafiaResultDirectory, null, null);

        assertNotNull(files);
        assertTrue(files.size() > 1);
    }
}
