package nl.sijpesteijn.plugins.testing.fitnesse;

import java.io.File;
import java.util.Map;

import nl.sijpesteijn.testing.fitnesse.plugins.FitnesseReportMojo;

/**
 * 
 * Test only covers configuration test. Execution test is done in
 * {@link nl.sijpesteijn.plugins.testing.fitnesse.IntegrationTest}
 * 
 */
public class FitNesseReportMojoTest extends AbstractFitNesseTestCase {
    private FitnesseReportMojo mojo;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mojo = configureFitNesseMojo(new FitnesseReportMojo(), "report");
    }

    @SuppressWarnings("rawtypes")
    public void testConfiguration() throws Exception {
        final Map map = getVariablesAndValuesFromObject(mojo);
        final File outputDirectory = (File) map.get("outputDirectory");
        assertTrue(outputDirectory.getAbsolutePath().equals(getTestDirectory() + TARGET + "/fitnesse"));
        final String testResultsDirectory = (String) map.get("testResultsDirectory");
        assertTrue(testResultsDirectory.equals(getTestDirectory() + TARGET + "/fitnesse-run-results"));
        assertTrue(mojo.getDescription(null).equals(
                "Maven mafia plugin - reporting: Generate a report of the fitnessetests that have run"));
        assertTrue(mojo.getName(null).equals("Mafia Report"));
        assertNull(mojo.getProject());
    }
}
