package nl.sijpesteijn.plugins.testing.fitnesse;

import java.util.Map;

import nl.sijpesteijn.testing.fitnesse.plugins.FitnesseStopperMojo;

import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * 
 * Test only covers configuration test. Execution test is done in
 * {@link nl.sijpesteijn.plugins.testing.fitnesse.IntegrationTest}
 * 
 */
public class FitNesseStopperMojoTest extends AbstractFitNesseTestCase {
    private FitnesseStopperMojo mojo;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mojo = new FitnesseStopperMojo();
        final Xpp3Dom configuration = getPluginConfiguration("mafia-maven-plugin", "stop");
        setVariableValueToObject(mojo, "port", getStringValueFromConfiguration(configuration, "port", "9090"));
        setVariableValueToObject(mojo, "dependencies", model.getDependencies());
        setVariableValueToObject(mojo, "baseDir", REPO);
    }

    @SuppressWarnings("rawtypes")
    public void testConfiguration() throws Exception {
        final Map map = getVariablesAndValuesFromObject(mojo);
        final String port = (String) map.get("port");
        assertTrue(port.equals("9090"));
    }
}
