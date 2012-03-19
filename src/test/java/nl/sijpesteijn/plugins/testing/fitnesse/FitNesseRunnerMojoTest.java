package nl.sijpesteijn.plugins.testing.fitnesse;

import java.util.Map;

import nl.sijpesteijn.testing.fitnesse.plugins.FitnesseRunnerMojo;

/**
 * 
 * Test only covers configuration test. Execution test is done in
 * {@link nl.sijpesteijn.plugins.testing.fitnesse.IntegrationTest}
 */
public class FitNesseRunnerMojoTest extends AbstractFitNesseTestCase {
    private FitnesseRunnerMojo mojo;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mojo = configureFitNesseMojo(new FitnesseRunnerMojo(), "run");
    }

    @SuppressWarnings("rawtypes")
    public void testCheckArguments() throws Exception {
        final Map map = getVariablesAndValuesFromObject(mojo);
        final Integer port = (Integer) map.get("port");
        assertTrue(port == 9091);
        final String wikiRoot = (String) map.get("wikiRoot");
        assertTrue(wikiRoot.replace("\\", "/").equals(getTestDirectory() + TARGET));
        final String nameRootPage = (String) map.get("nameRootPage");
        assertTrue(nameRootPage.equals(FITNESSE_ROOT));
        final String fitnesseOutputDirectory = (String) map.get("fitnesseOutputDirectory");
        assertTrue(fitnesseOutputDirectory.replace("\\", "/").equals(
            getTestDirectory() + TARGET + "/fitnesse-run-results"));
        final String[] tests = (String[]) map.get("tests");
        assertTrue(tests[0].equals("FrontPage.BuyMilk"));
        final Boolean stopTestsOnFailure = (Boolean) map.get("stopTestsOnFailure");
        assertTrue(stopTestsOnFailure);
        final Boolean stopTestsOnIgnore = (Boolean) map.get("stopTestsOnIgnore");
        assertTrue(stopTestsOnIgnore);
        final Boolean stopTestsOnException = (Boolean) map.get("stopTestsOnException");
        assertTrue(stopTestsOnException);
        final Boolean stopTestsOnWrong = (Boolean) map.get("stopTestsOnWrong");
        assertTrue(stopTestsOnWrong);
    }
}
