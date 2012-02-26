package nl.sijpesteijn.plugins.testing.fitnesse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.sijpesteijn.testing.fitnesse.plugins.FitnesseStarterMojo;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

public class FitNesseStarterMojoTest extends AbstractFitNesseTestCase {
	private FitnesseStarterMojo mojo;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mojo = configureFitNesseMojo(new FitnesseStarterMojo(), "starter");
		setVariableValueToObject(mojo, "dependencies", createDependencies());
		setVariableValueToObject(mojo, "baseDir", REPO);
		setVariableValueToObject(mojo, "jvmArguments", new String[0]);
		setVariableValueToObject(mojo, "jvmDependencies", new Dependency[0]);
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
		final String retainDays = (String) map.get("retainDays");
		final String wikiRoot = (String) map.get("wikiRoot");
		final String nameRootPage = (String) map.get("nameRootPage");
		assertTrue(port.equals("9090"));
		assertTrue(retainDays.equals("14"));
		assertTrue(wikiRoot.equals(getTestDirectory() + "target/"));
		assertTrue(nameRootPage.equals("FitNesseRoot"));
	}

	public void testExecute() throws Exception {
		try {
			mojo.execute();
		} catch (MojoExecutionException mee) {
			fail(mee.getMessage());
		} catch (MojoFailureException mfe) {
			fail(mfe.getMessage());
		}
	}
}
