package nl.sijpesteijn.plugins.testing.fitnesse;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;

public abstract class AbstractFitNesseTestCase<F extends Mojo> extends AbstractMojoTestCase {

    protected static final String LOG4J_JAR = "log4j/log4j/1.2.15/log4j-1.2.15.jar";
    protected static final String JUNIT_JAR = "junit/junit/4.8.2/junit-4.8.2.jar";
    protected static final String TARGET_CLASSES = "target/classes";

    private PlexusConfiguration plexusConfiguration;
    private String testDirectory;
    protected String REPO;

    @SuppressWarnings("unchecked")
    F configureFitNesseMojo(final F fitNesseMojo) throws Exception {
        return (F) configureMojo(fitNesseMojo, plexusConfiguration);
    }

    protected void setUp(final String goal) throws Exception {
        final File pom = new File(getBasedir(), "src/test/resources/mafia-plugin-" + goal + ".xml");
        testDirectory = new File("").getAbsolutePath() + File.separatorChar;
        REPO =
                testDirectory + "src" + File.separatorChar + "test" + File.separatorChar + "resources"
                        + File.separatorChar + "repo" + File.separatorChar;

        final FileInputStream fis = new FileInputStream(pom);
        final Xpp3Dom dom = Xpp3DomBuilder.build(fis, "utf-8");
        plexusConfiguration = this.extractPluginConfiguration("mafia-maven-plugin", dom);
        super.setUp();
    }

    protected MavenProjectStub createProjectStub() {
        final MavenProjectStub projectStub = new MavenProjectStub();
        final List<String> compileSourceRoots = new ArrayList<String>();
        compileSourceRoots.add(REPO + JUNIT_JAR);
        compileSourceRoots.add(REPO + LOG4J_JAR);
        compileSourceRoots.add(getTestDirectory() + TARGET_CLASSES);
        projectStub.setCompileSourceRoots(compileSourceRoots);
        return projectStub;
    }

    public String getTestDirectory() {
        return testDirectory;
    }
}
