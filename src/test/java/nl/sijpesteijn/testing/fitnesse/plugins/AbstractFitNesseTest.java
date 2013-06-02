package nl.sijpesteijn.testing.fitnesse.plugins;

import nl.sijpesteijn.testing.fitnesse.plugins.runner.FitNesseCommanderConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.stub.LoggerStub;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * User: gijs
 * Date: 5/10/13 7:03 PM
 */
public class AbstractFitNesseTest {
    protected final String base;
    protected final int PORT = 9999;
    protected final String WIKI_ROOT;
    protected final String NAME_ROOT_PAGE = "FitNesseRoot";
    protected final String LOG_DIRECTORY;
    protected final String FITNESSE_JAR_PATH;
    protected final List<String> jvmArguments = new ArrayList<String>() {{add("-Dmaven.test.skip=true");add("maven.javadoc.skip=false");}};
    protected final LoggerStub mockLog = new LoggerStub();
    protected final String TEST_RESULT_DIR;
    protected final String SEP = File.separator;

    public AbstractFitNesseTest() {
        base = new File("").getAbsolutePath();
        WIKI_ROOT = base + SEP + "target" + SEP;
        LOG_DIRECTORY = base + SEP +  "target" + SEP + "log";
        FITNESSE_JAR_PATH = base + SEP + "src" + SEP + "test" + SEP + "resources"  + SEP + "fitnesse.jar";
        TEST_RESULT_DIR = base +  SEP + "target" + SEP + "test-results" + SEP;
    }


    protected FitNesseCommanderConfig getFitnesseCommanderConfig() {
        return new FitNesseCommanderConfig(PORT, WIKI_ROOT, NAME_ROOT_PAGE, LOG_DIRECTORY,0,
                FITNESSE_JAR_PATH, jvmArguments, mockLog);
    }
}
