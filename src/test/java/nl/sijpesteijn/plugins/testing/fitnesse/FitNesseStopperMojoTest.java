package nl.sijpesteijn.plugins.testing.fitnesse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.sijpesteijn.testing.fitnesse.plugins.FitnesseStopperMojo;

import org.apache.maven.model.Dependency;

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
		List<Dependency> dependencies = new ArrayList<Dependency>();
		Dependency fitnesse = new Dependency();
		fitnesse.setArtifactId("fitnesse");
		fitnesse.setGroupId("org.fitnesse");
		fitnesse.setVersion("20111025");
		dependencies.add(fitnesse);
		return dependencies;
	}

	public void testConfiguration() throws Exception {
		final Map map = getVariablesAndValuesFromObject(mojo);
		final String port = (String) map.get("port");
		assertTrue(port.equals("9090"));
	}

}
