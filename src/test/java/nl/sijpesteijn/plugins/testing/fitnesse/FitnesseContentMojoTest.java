package nl.sijpesteijn.plugins.testing.fitnesse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.sijpesteijn.testing.fitnesse.plugins.FitnesseContentMojo;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.apache.maven.plugin.testing.stubs.StubArtifactRepository;

public class FitnesseContentMojoTest extends AbstractFitNesseTestCase<FitnesseContentMojo> {
    private static final String FITNESSE_ROOT = "FitNesseRoot";
    private static final String TARGET = "/target/";
    private FitnesseContentMojo mojo;
    private String testDirectory;

    @Override
    protected void setUp() throws Exception {
        setUp("content");
        testDirectory = new File("").getAbsolutePath();
        mojo = configureFitNesseMojo(new FitnesseContentMojo());
        this.setVariableValueToObject(mojo, "project", createProjectStub());
        this.setVariableValueToObject(mojo, "local", new StubArtifactRepository(testDirectory));
        this.setVariableValueToObject(mojo, "wikiRoot", testDirectory + TARGET);
        this.setVariableValueToObject(mojo, "nameRootPage", FITNESSE_ROOT);
    }

    private MavenProjectStub createProjectStub() {
        final MavenProjectStub projectStub = new MavenProjectStub();
        final List<Dependency> dependencies = new ArrayList<Dependency>();
        final Dependency dependency1 = new Dependency();
        dependency1.setGroupId("com.example");
        dependency1.setArtifactId("group1");
        dependency1.setVersion("1");
        dependency1.setType("war");
        dependencies.add(dependency1);
        final Dependency dependency2 = new Dependency();
        dependency2.setGroupId("com.example");
        dependency2.setArtifactId("group2");
        dependency2.setVersion("1");
        dependencies.add(dependency2);
        final Dependency dependency3 = new Dependency();
        dependency3.setGroupId("com.example");
        dependency3.setArtifactId("group3");
        dependency3.setVersion("1");
        dependencies.add(dependency3);
        projectStub.setCompileSourceRoots(dependencies);
        return projectStub;
    }

    @SuppressWarnings("rawtypes")
    public void testCheckArguments() throws Exception {
        final Map map = getVariablesAndValuesFromObject(mojo);
        final String[] statics = (String[]) map.get("statics");
        final String[] resources = (String[]) map.get("resources");
        final String[] targets = (String[]) map.get("targets");
        final Dependency[] excludeDependencies = (Dependency[]) map.get("excludeDependencies");
        assertTrue(statics.length == 1);
        assertTrue(statics[0].equals("!define TEST_SYSTEM {slim}"));
        assertTrue(resources.length == 1);
        assertTrue(resources[0].equals("./src/main/resources"));
        assertTrue(targets.length == 1);
        assertTrue(targets[0].equals("./domain"));
        assertTrue(excludeDependencies.length == 2);
        assertTrue(excludeDependencies[0].toString().equals(
            "Dependency {groupId=group1, artifactId=com.example, version=1, type=war}"));
        assertTrue(excludeDependencies[1].toString().equals(
            "Dependency {groupId=group2, artifactId=com.example, version=1, type=jar}"));
    }

    public void testContentFile() throws Exception {
        mojo.execute();
        final String content = getContentFile();
        assertTrue(content.contains("!define TEST_SYSTEM {slim}"));
        assertTrue(content.contains("!path ./src/main/resources"));
        assertTrue(content.contains("!path ./domain/target/classes/"));
    }

    private String getContentFile() throws Exception {
        final StringBuilder contents = new StringBuilder();
        final File file = new File(testDirectory + TARGET + FITNESSE_ROOT + "/content.txt");
        final BufferedReader input = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line = input.readLine()) != null) {
            contents.append(line);
        }
        input.close();
        return contents.toString();

    }
}
