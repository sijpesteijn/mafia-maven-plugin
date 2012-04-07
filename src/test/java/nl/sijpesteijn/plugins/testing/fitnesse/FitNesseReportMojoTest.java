package nl.sijpesteijn.plugins.testing.fitnesse;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.File;
import java.io.OutputStreamWriter;
import java.util.Map;

import nl.sijpesteijn.testing.fitnesse.plugins.FitnesseReportMojo;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.doxia.siterenderer.SiteRenderingContext;
import org.apache.maven.doxia.siterenderer.sink.SiteRendererSink;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * 
 * Test only covers configuration test. Execution test is done in
 * {@link nl.sijpesteijn.plugins.testing.fitnesse.IntegrationTest}
 * 
 */
public class FitNesseReportMojoTest extends AbstractFitNesseTestCase {
	private FitnesseReportMojo mojo;
	private Renderer rendererMock;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mojo = new FitnesseReportMojo();
		final Xpp3Dom configuration = getPluginConfiguration("mafia-maven-plugin", "report");
		setVariableValueToObject(
				mojo,
				"outputDirectory",
				new File(
						getStringValueFromConfiguration(configuration, "outputDirectory", "${basedir}/target/fitnesse")));
		setVariableValueToObject(
				mojo,
				"mafiaTestResultsDirectory",
				getStringValueFromConfiguration(configuration, "mafiaTestResultsDirectory",
						"${basedir}/target/FitNesseRoot/files/mafiaTestResults"));
		setVariableValueToObject(mojo, "suites", getStringArrayFromConfiguration(configuration, "suites"));
		setVariableValueToObject(mojo, "tests", getStringArrayFromConfiguration(configuration, "tests"));
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

	public void testReportGeneration() throws Exception {
		rendererMock = createMock(Renderer.class);
		rendererMock.generateDocument(isA(OutputStreamWriter.class), isA(SiteRendererSink.class),
				isA(SiteRenderingContext.class));
		expectLastCall();
		replay(rendererMock);
		setVariableValueToObject(mojo, "siteRenderer", rendererMock);
		mojo.execute();
		verify(rendererMock);

	}
}
