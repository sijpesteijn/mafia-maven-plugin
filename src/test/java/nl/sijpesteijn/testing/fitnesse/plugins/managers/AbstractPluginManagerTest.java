package nl.sijpesteijn.testing.fitnesse.plugins.managers;

import java.io.File;

import org.junit.Before;

public class AbstractPluginManagerTest {
    protected String REPO;
    protected String testDirectory;
    protected static final String LOG4J_JAR = "log4j/log4j/1.2.15/log4j-1.2.15.jar";
    protected static final String JUNIT_JAR = "junit/junit/4.8.2/junit-4.8.2.jar";
    protected static final String FITNESSE_JAR = "org/fitnesse/fitnesse/20111025/fitnesse-20111025.jar";

    @Before
    public void setup() {
        testDirectory = new File("").getAbsolutePath() + File.separatorChar;
        REPO = testDirectory + "src" + File.separatorChar + "test" + File.separatorChar + "resources"
                + File.separatorChar + "repo" + File.separatorChar;
    }
}
