package nl.sijpesteijn.plugins.testing.fitnesse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.sijpesteijn.testing.fitnesse.plugins.FitnesseStarterMojo;

import org.apache.maven.model.Dependency;

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
        mojo = configureFitNesseMojo(new FitnesseStarterMojo(), "starter");
        final Dependency velocity = new Dependency();
        velocity.setGroupId("org.apache.velocity");
        velocity.setArtifactId("velocity");
        velocity.setVersion("1.7");
        setVariableValueToObject(mojo, "dependencies", createDependencies());
        setVariableValueToObject(mojo, "baseDir", new File(REPO));
        setVariableValueToObject(mojo, "jvmArguments", new String[0]);
        final Dependency[] jvmDependencies = new Dependency[1];
        jvmDependencies[0] = velocity;
        setVariableValueToObject(mojo, "jvmDependencies", jvmDependencies);
    }

    private List<Dependency> createDependencies() {
        final List<Dependency> dependencies = new ArrayList<Dependency>();
        final Dependency fitnesse = new Dependency();
        fitnesse.setArtifactId("fitnesse");
        fitnesse.setGroupId("org.fitnesse");
        fitnesse.setVersion("20111025");
        dependencies.add(fitnesse);
        return dependencies;
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
