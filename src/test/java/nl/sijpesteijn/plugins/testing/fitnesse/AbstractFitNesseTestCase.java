package nl.sijpesteijn.plugins.testing.fitnesse;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public abstract class AbstractFitNesseTestCase extends AbstractMojoTestCase {

	protected static final String LOG4J_JAR = "log4j/log4j/1.2.15/log4j-1.2.15.jar";
	protected static final String JUNIT_JAR = "junit/junit/4.8.2/junit-4.8.2.jar";
	protected static final String FITNESSE_JAR = "org/fitnesse/fitnesse/20111025/fitnesse-20111025.jar";
	protected static final String TARGET_CLASSES = "target/classes";
	protected static final String FITNESSE_ROOT = "FitNesseRoot";
	protected static final String TARGET = "/target";

	private String testDirectory;
	protected String REPO;

	@SuppressWarnings("unchecked")
	<F extends Mojo> F configureFitNesseMojo(final F fitNesseMojo, final String goal) throws Exception {
		final File pom = new File(getBasedir(), "pom.xml");

		return (F) configureMojo(fitNesseMojo, extractPluginConfiguration("mafia-maven-plugin", pom));
	}

	@Override
	protected void setUp() throws Exception {
		testDirectory = new File("").getAbsolutePath().replace("\\", "/");
		REPO = testDirectory + "/src/test/resources/repo";
		final File pom = new File(getBasedir(), "pom.xml");
		final MavenXpp3Reader reader = new MavenXpp3Reader();
		final Model model = reader.read(new InputStreamReader(new FileInputStream(pom)));
		super.setUp();
	}

	public String getTestDirectory() {
		return testDirectory;
	}
}
