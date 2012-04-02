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
		assertTrue(outputDirectory.getAbsolutePath().replace('\\', '/')
				.equals(getTestDirectory() + TARGET + "/fitnesse"));
		final String mafiaTestResultsDirectory = (String) map.get("mafiaTestResultsDirectory");
		assertTrue(mafiaTestResultsDirectory.replace('\\', '/').equals(
				getTestDirectory() + TARGET + "/FitNesseRoot/files/mafiaTestResults"));
		final String[] suites = (String[]) map.get("suites");
		assertTrue(suites[0].equals("FrontPage.BuyMilkSuite"));
		assertTrue(mojo.getDescription(null).equals(
				"Maven mafia plugin - reporting: Generate a report of the fitnessetests that have run"));
		assertTrue(mojo.getName(null).equals("Mafia Report"));
		assertNull(mojo.getProject());
	}
}
