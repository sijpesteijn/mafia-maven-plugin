package nl.sijpesteijn.plugins.testing.fitnesse;

import java.io.File;
import java.util.Map;

import nl.sijpesteijn.testing.fitnesse.plugins.FitnesseStarterMojo;

import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * 
 * Test only covers configuration test. Execution test is done in
 * {@link nl.sijpesteijn.plugins.testing.fitnesse.IntegrationTest}
 */
public class FitNesseStarterMojoTest extends AbstractFitNesseTestCase {
    private FitnesseStarterMojo mojo;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mojo = new FitnesseStarterMojo();
        final Xpp3Dom configuration = getPluginConfiguration("mafia-maven-plugin", "start");
        setVariableValueToObject(mojo, "dependencies", model.getDependencies());
        setVariableValueToObject(mojo, "baseDir", new File(REPO));
        setVariableValueToObject(mojo, "jvmArguments", new String[0]);
        setVariableValueToObject(mojo, "port", getStringValueFromConfiguration(configuration, "port", "9090"));
        setVariableValueToObject(mojo, "retainDays", getStringValueFromConfiguration(configuration, "retainDays", "14"));
        setVariableValueToObject(mojo, "wikiRoot",
            getStringValueFromConfiguration(configuration, "wikiRoot", "${basedir}/target"));
        setVariableValueToObject(mojo, "nameRootPage",
            getStringValueFromConfiguration(configuration, "nameRootPage", "FitNesseRoot"));
    }

    @SuppressWarnings("rawtypes")
    public void testConfiguration() throws Exception {
        final Map map = getVariablesAndValuesFromObject(mojo);
        final String port = (String) map.get("port");
        final String retainDays = (String) map.get("retainDays");
        final String wikiRoot = (String) map.get("wikiRoot");
        final String nameRootPage = (String) map.get("nameRootPage");
        assertTrue(port.equals("9090"));
        assertTrue(retainDays.equals("14"));
        assertTrue(wikiRoot.replace("\\", "/").equals(getTestDirectory() + TARGET));
        assertTrue(nameRootPage.equals("FitNesseRoot"));
    }
}
