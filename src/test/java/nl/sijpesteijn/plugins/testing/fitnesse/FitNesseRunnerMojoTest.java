package nl.sijpesteijn.plugins.testing.fitnesse;

import java.util.Map;

import nl.sijpesteijn.testing.fitnesse.plugins.FitnesseRunnerMojo;

import org.codehaus.plexus.util.xml.Xpp3Dom;

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
        mojo = new FitnesseRunnerMojo();
        final Xpp3Dom configuration = getPluginConfiguration("mafia-maven-plugin", "run-tests");
        setVariableValueToObject(mojo, "port", getStringValueFromConfiguration(configuration, "port", "9091"));
        setVariableValueToObject(mojo, "wikiRoot",
            getStringValueFromConfiguration(configuration, "wikiRoot", "${basedir}/target"));
        setVariableValueToObject(mojo, "nameRootPage",
            getStringValueFromConfiguration(configuration, "nameRootPage", "FitNesseRoot"));
        setVariableValueToObject(mojo, "mafiaTestResultsDirectory",
            getStringValueFromConfiguration(configuration, "mafiaTestResultsDirectory", "mafiaTestResults"));
        setVariableValueToObject(mojo, "tests", getStringArrayFromConfiguration(configuration, "tests"));
        setVariableValueToObject(mojo, "suites", getStringArrayFromConfiguration(configuration, "suites"));
        setVariableValueToObject(mojo, "stopTestsOnFailure", true);
        setVariableValueToObject(mojo, "stopTestsOnIgnore", true);
        setVariableValueToObject(mojo, "stopTestsOnException", true);
        setVariableValueToObject(mojo, "stopTestsOnWrong", true);
    }

    @SuppressWarnings("rawtypes")
    public void testCheckArguments() throws Exception {
        final Map map = getVariablesAndValuesFromObject(mojo);
        final String port = (String) map.get("port");
        assertTrue(port.equals("9091"));
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
}
