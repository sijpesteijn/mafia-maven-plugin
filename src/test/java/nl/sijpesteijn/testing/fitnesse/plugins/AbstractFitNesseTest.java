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
    protected static final String base = new File("").getAbsolutePath();
    protected static final String SEP = File.separator;
    protected static final int PORT = 9091;
    protected static final String WIKI_ROOT = base + SEP + "target" + SEP;
    protected static final String NAME_ROOT_PAGE = "FitNesseRoot";
    protected static final String LOG_DIRECTORY = base + SEP +  "target" + SEP + "log";
    protected static final String FITNESSE_JAR_PATH = base + SEP + "src" + SEP + "test" + SEP + "resources"  + SEP + "fitnesse.jar";
    protected static final List<String> jvmArguments = new ArrayList<String>() {{add("-Dmaven.test.skip=true");add("maven.javadoc.skip=false");}};
    protected static final LoggerStub mockLog = new LoggerStub();
    protected static final String TEST_RESULT_DIR  = base +  SEP + "target" + SEP + "test-results" + SEP;

    public static FitNesseCommanderConfig getFitnesseCommanderConfig() {
        return new FitNesseCommanderConfig(PORT, WIKI_ROOT, NAME_ROOT_PAGE, LOG_DIRECTORY,0,
                FITNESSE_JAR_PATH, jvmArguments, 3000 ,mockLog, null, null);
    }
}
