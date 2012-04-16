package nl.sijpesteijn.plugins.testing.fitnesse;

import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.Writer;
import java.util.Map;

import nl.sijpesteijn.testing.fitnesse.plugins.FitNesseContentMojo;
import nl.sijpesteijn.testing.fitnesse.plugins.FitNesseReportMojo;
import nl.sijpesteijn.testing.fitnesse.plugins.FitNesseRunnerMojo;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.FirstTimeWriter;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.FitNesseExtractor;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * Test ReporterMojo
 * 
 */
public class FitNesseReportMojoTest extends AbstractFitNesseTestCase {
    private FitNesseReportMojo reporterMojo;
    private FitNesseRunnerMojo runnerMojo;
    private FitNesseContentMojo contentMojo;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        runnerMojo = configureRunnerMojo();
        reporterMojo = configureReporterMojo();
        contentMojo = configureContentMojo();
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testConfiguration() throws Exception {
        final Map map = getVariablesAndValuesFromObject(reporterMojo);
        final File outputDirectory = (File) map.get("outputDirectory");
        assertTrue(outputDirectory.getAbsolutePath().replace('\\', '/').equals(getTestDirectory() + TARGET + "/site"));
        final String mafiaTestResultsDirectory = (String) map.get("mafiaTestResultsDirectory");
        assertTrue(mafiaTestResultsDirectory.replace('\\', '/').equals(
                getTestDirectory() + TARGET + "/" + FITNESSE_ROOT + "/files/" + MAFIA_TEST_RESULTS));
        // final String[] suites = (String[]) map.get("suites");
        // assertTrue(suites[0].equals("FrontPage.BuyMilkSuite"));
        assertTrue(reporterMojo.getDescription(null).equals(
                "Maven mafia plugin - reporting: Generate a report of the fitnessetests that have run"));
        assertTrue(reporterMojo.getName(null).equals("Mafia Report"));
        assertNull(reporterMojo.getProject());
    }

    @Test(expected = MojoExecutionException.class)
    public void checkNoFitNesseNoReports() throws Exception {
        deleteTestDirectory();
        reporterMojo.execute();
    }

    @Test(expected = MojoExecutionException.class)
    public void checkNoReportsGenerated() throws Exception {
        deleteTestDirectory();

        FitNesseExtractor.extract(new SystemStreamLog(), getTestDirectory() + "/target/", REPO);

        contentMojo.execute();
        new FirstTimeWriter(getTestDirectory() + "/target/" + FITNESSE_ROOT);

        reporterMojo.execute();
    }

    @Test
    public void checkSingleTestReport() throws Exception {
        deleteTestDirectory();

        FitNesseExtractor.extract(new SystemStreamLog(), getTestDirectory() + "/target/", REPO);

        contentMojo.execute();
        new FirstTimeWriter(getTestDirectory() + "/target/" + FITNESSE_ROOT);

        contentMojo.execute();
        createDummySuite();
        createDummyTest("");
        createDummyTest("1");

        runnerMojo.execute();

        replay(rendererMock);
        reporterMojo.execute();
        verify(rendererMock);
        final Sink value = (Sink) this.getVariableValueFromObject(reporterMojo, "sink");
        final Writer writer = (Writer) this.getVariableValueFromObject(value, "writer");

        final String actual = writer.toString();
        final String expected = FileUtils.fileRead(new File(getTestDirectory() + "/src/test/resources/report.html"));

        assertNotNull(actual);
        // assertTrue(actual.equals(expected));
    }
}
