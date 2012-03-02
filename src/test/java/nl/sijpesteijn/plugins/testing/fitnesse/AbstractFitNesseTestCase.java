package nl.sijpesteijn.plugins.testing.fitnesse;

import java.io.File;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public abstract class AbstractFitNesseTestCase extends AbstractMojoTestCase {

	protected static final String LOG4J_JAR = "log4j/log4j/1.2.15/log4j-1.2.15.jar";
	protected static final String JUNIT_JAR = "junit/junit/4.8.2/junit-4.8.2.jar";
	protected static final String FITNESSE_JAR = "org/fitnesse/fitnesse/20111025/fitnesse-20111025.jar";
	protected static final String TARGET_CLASSES = "target/classes";
	protected static final String FITNESSE_ROOT = "FitNesseRoot";
	protected static final String TARGET = "target";

	private String testDirectory;
	protected String REPO;

	@SuppressWarnings("unchecked")
	<F extends Mojo> F configureFitNesseMojo(final F fitNesseMojo, final String goal) throws Exception {
		final File pom = new File(getBasedir(), "src/test/resources/mafia-plugin-" + goal + ".xml");
		return (F) configureMojo(fitNesseMojo, extractPluginConfiguration("mafia-maven-plugin", pom));
	}

	@Override
	protected void setUp() throws Exception {
		testDirectory = new File("").getAbsolutePath() + File.separatorChar;
		REPO = testDirectory + "src" + File.separatorChar + "test" + File.separatorChar + "resources"
				+ File.separatorChar + "repo" + File.separatorChar;
		super.setUp();
	}

	public String getTestDirectory() {
		return testDirectory;
	}
}
