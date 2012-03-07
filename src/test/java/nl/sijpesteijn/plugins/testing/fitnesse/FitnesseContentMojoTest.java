package nl.sijpesteijn.plugins.testing.fitnesse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.sijpesteijn.testing.fitnesse.plugins.FitnesseContentMojo;

import org.apache.maven.model.Dependency;

/**
 * 
 * This test covers configuration test and output verifications
 * 
 */
public class FitnesseContentMojoTest extends AbstractFitNesseTestCase {
    private static final String FITNESSE_ROOT = "FitNesseRoot";
    private static final String TARGET = "/target/";
    private FitnesseContentMojo mojo;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mojo = configureFitNesseMojo(new FitnesseContentMojo(), "content");
        setVariableValueToObject(mojo, "compileClasspathElements", createCompileClasspathElements());
        setVariableValueToObject(mojo, "baseDir", REPO);
        setVariableValueToObject(mojo, "wikiRoot", getTestDirectory() + TARGET);
        setVariableValueToObject(mojo, "nameRootPage", FITNESSE_ROOT);
    }

    private List<String> createCompileClasspathElements() {
        final List<String> compileSourceRoots = new ArrayList<String>();
        compileSourceRoots.add(REPO + JUNIT_JAR);
        compileSourceRoots.add(REPO + LOG4J_JAR);
        compileSourceRoots.add(getTestDirectory() + TARGET_CLASSES);
        return compileSourceRoots;
    }

    @SuppressWarnings("rawtypes")
    public void testCheckArguments() throws Exception {
        final Map map = getVariablesAndValuesFromObject(mojo);
        final String[] statics = (String[]) map.get("statics");
        final String[] resources = (String[]) map.get("resources");
        final String[] targets = (String[]) map.get("targets");
        final Dependency[] excludeDependencies = (Dependency[]) map.get("excludeDependencies");
        assertTrue(statics.length == 2);
        assertTrue(statics[0].equals("!define TEST_SYSTEM {slim}"));
        assertTrue(statics[1].equals("!define fixturePackage {fitnesse.slim.test}"));
        assertTrue(resources.length == 1);
        assertTrue(resources[0].equals(getTestDirectory() + "/src/main/resources"));
        assertTrue(targets.length == 1);
        assertTrue(targets[0].equals("./domain"));
        assertTrue(excludeDependencies.length == 1);
        assertTrue(excludeDependencies[0].toString().equals(
            "Dependency {groupId=log4j, artifactId=log4j, version=1.2.15, type=jar}"));
    }

    public void testContentFile() throws Exception {
        mojo.execute();
        final String content = getContentFile();
        assertTrue(content.contains("!define TEST_SYSTEM {slim}"));
        assertTrue(content.contains("!path " + getTestDirectory() + "/src/main/resources"));
        assertTrue(content.contains("!path ./domain/target/classes/"));
        assertTrue(content.contains("!path " + REPO + JUNIT_JAR));
        assertFalse(content.contains("!path " + REPO + LOG4J_JAR));
    }

    private String getContentFile() throws Exception {
        final StringBuilder contents = new StringBuilder();
        final File file = new File(getTestDirectory() + TARGET + FITNESSE_ROOT + "/content.txt");
        final BufferedReader input = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line = input.readLine()) != null) {
            contents.append(line);
        }
        input.close();
        return contents.toString();
    }
}
