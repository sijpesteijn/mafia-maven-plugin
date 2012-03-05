package nl.sijpesteijn.plugins.testing.fitnesse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.sijpesteijn.testing.fitnesse.plugins.FitnesseStopperMojo;

import org.apache.maven.model.Dependency;

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
		mojo = configureFitNesseMojo(new FitnesseStopperMojo(), "stop");
		setVariableValueToObject(mojo, "dependencies", createDependencies());
		setVariableValueToObject(mojo, "baseDir", REPO);
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
		assertTrue(port.equals("9090"));
	}
}
