package nl.sijpesteijn.plugins.testing.fitnesse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;

import nl.sijpesteijn.testing.fitnesse.plugins.FitNesseContentMojo;

import org.apache.maven.model.Dependency;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * This test covers configuration test and output verifications
 * 
 */
public class FitnesseContentMojoTest extends AbstractFitNesseTestCase {
    private static final String FITNESSE_ROOT = "FitNesseRoot";
    private static final String TARGET = "/target/";
    private FitNesseContentMojo contentMojo;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        contentMojo = configureContentMojo();
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testCheckArguments() throws Exception {
        final Map map = getVariablesAndValuesFromObject(contentMojo);
        final String[] statics = (String[]) map.get("statics");
        final String[] resources = (String[]) map.get("resources");
        final String[] targets = (String[]) map.get("targets");
        final Dependency[] excludeDependencies = (Dependency[]) map.get("excludeDependencies");
        assertTrue(statics.length == 2);
        assertTrue(statics[0].equals("!define TEST_SYSTEM {slim}"));
        assertTrue(statics[1].equals("!define fixturePackage {fitnesse.slim.test}"));
        assertTrue(resources.length == 1);
        assertTrue(resources[0].replace("\\", "/").equals(getTestDirectory() + "/src/main/resources"));
        assertTrue(targets.length == 1);
        assertTrue(targets[0].equals("./domain"));
        assertTrue(excludeDependencies.length == 1);
        assertTrue(excludeDependencies[0].toString().equals(
                "Dependency {groupId=log4j, artifactId=log4j, version=1.2.15, type=jar}"));
    }

    @Test
    public void testContentFile() throws Exception {
        deleteTestDirectory();
        contentMojo.execute();
        final String content = getContentFile();
        assertTrue(content.contains("!define TEST_SYSTEM {slim}"));
        assertTrue(content.contains("/src/main/resources"));
        assertTrue(content.contains("!path ./domain/target/classes/"));
        assertTrue(content.contains("!path " + REPO + "/" + JUNIT_JAR));
        assertFalse(content.contains("!path " + REPO + "/" + LOG4J_JAR));
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
