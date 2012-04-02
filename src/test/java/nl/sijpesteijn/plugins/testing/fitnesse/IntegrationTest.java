package nl.sijpesteijn.plugins.testing.fitnesse;

import static org.easymock.EasyMock.createMock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.sijpesteijn.testing.fitnesse.plugins.FitnesseContentMojo;
import nl.sijpesteijn.testing.fitnesse.plugins.FitnesseReportMojo;
import nl.sijpesteijn.testing.fitnesse.plugins.FitnesseRunnerMojo;
import nl.sijpesteijn.testing.fitnesse.plugins.FitnesseStarterMojo;
import nl.sijpesteijn.testing.fitnesse.plugins.FitnesseStopperMojo;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.SpecialPages;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

public class IntegrationTest extends AbstractFitNesseTestCase {

	private FitnesseContentMojo contentMojo;
	private FitnesseStarterMojo starterMojo;
	private FitnesseRunnerMojo runnerMojo;
	private FitnesseReportMojo reportMojo;
	private FitnesseStopperMojo stopperMojo;
	private Renderer rendererMock;

	@Test
	public void testSetupPluginIntegration() throws Exception {
		setupFitNesseStarterMojo();
		setupFitNesseContentMojo();
		setupFitNesseRunnerMojo();
		setupFitNesseReportMojo();
		setupFitNesseStopperMojo();

		// Stop if any fitnesse instance is running
		stopFitNesse();
		starterMojo.execute();
		stopperMojo.execute();
		contentMojo.execute();
		createDummySuite();
		createDummyTest("");
		createDummyTest("1");
		runnerMojo.execute();

		// rendererMock.generateDocument(isA(OutputStreamWriter.class),
		// isA(SiteRendererSink.class),
		// isA(SiteRenderingContext.class));
		// expectLastCall();
		// replay(rendererMock);
		// reportMojo.execute();
		// verify(rendererMock);
		//
		// final String report = getReport();
		// assertTrue(report.contains("success: 2"));
		// assertTrue(report.contains("failure: 0"));
		// assertTrue(report.contains("exceptions: 0"));
		// assertTrue(report.contains("ignores: 0"));
	}

	private void createDummySuite() throws IOException {
		final String nameRootPage = getTestDirectory() + TARGET + File.separatorChar + FITNESSE_ROOT;
		final File setupDir = new File(nameRootPage + "/FrontPage/BuyMilkSuite/");
		setupDir.mkdirs();
		final File contentTxtFile = new File(nameRootPage + "/FrontPage/BuyMilkSuite/" + "content.txt");
		final FileWriter contentFileWriter = new FileWriter(contentTxtFile);
		contentFileWriter.write("!contents -r");
		contentFileWriter.close();
		final File propertiesXmlFile = new File(nameRootPage + "/FrontPage/BuyMilkSuite/" + "properties.xml");
		final FileWriter propertiesFileWriter = new FileWriter(propertiesXmlFile);
		propertiesFileWriter.write(SpecialPages.PropertiesXml.replace("</properties>", "<Suite/></properties>"));
		propertiesFileWriter.close();
	}

	private String getReport() throws IOException, MojoFailureException {
		final StringBuilder contents = new StringBuilder();
		final File file = new File(getTestDirectory() + TARGET + "/fitnesse/" + FitnesseReportMojo.OUTPUT_NAME
				+ ".html");
		if (!file.exists()) {
			throw new MojoFailureException(getTestDirectory() + TARGET + "/fitnesse/" + FitnesseReportMojo.OUTPUT_NAME
					+ ".html not found.");
		}
		final BufferedReader input = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = input.readLine()) != null) {
			contents.append(line);
		}
		input.close();
		return contents.toString();

	}

	private void createDummyTest(final String sub) throws IOException {
		final StringBuilder dummytest = new StringBuilder();
		dummytest.append("|should I buy milk|\n")
				.append("|cash in wallet|credit card|pints of milk remaining|go to store?|\n")
				.append("|      0       |    no     |      0                |    no      |\n")
				.append("|      10      |    no     |      0                |    yes     |\n");
		final String nameRootPage = getTestDirectory() + TARGET + File.separatorChar + FITNESSE_ROOT;
		final File setupDir = new File(nameRootPage + "/FrontPage/BuyMilkSuite/BuyMilk" + sub + "/");
		setupDir.mkdirs();
		final File contentTxtFile = new File(nameRootPage + "/FrontPage/BuyMilkSuite/BuyMilk" + sub + "/"
				+ "content.txt");
		final FileWriter contentFileWriter = new FileWriter(contentTxtFile);
		contentFileWriter.write(dummytest.toString());
		contentFileWriter.close();
		final File propertiesXmlFile = new File(nameRootPage + "/FrontPage/BuyMilkSuite/BuyMilk" + sub + "/"
				+ "properties.xml");
		final FileWriter propertiesFileWriter = new FileWriter(propertiesXmlFile);
		propertiesFileWriter.write(SpecialPages.PropertiesXml.replace("</properties>", "<Test/></properties>"));
		propertiesFileWriter.close();
	}

	private void stopFitNesse() {
		try {
			stopperMojo.execute();
		} catch (final Exception e) {

		}
	}

	private void setupFitNesseStopperMojo() throws Exception {
		stopperMojo = configureFitNesseMojo(new FitnesseStopperMojo(), "stop");
		setVariableValueToObject(stopperMojo, "dependencies", createDependencies());
		setVariableValueToObject(stopperMojo, "baseDir", REPO);
	}

	private void setupFitNesseReportMojo() throws Exception {
		reportMojo = configureFitNesseMojo(new FitnesseReportMojo(), "report");
		rendererMock = createMock(Renderer.class);
		setVariableValueToObject(reportMojo, "siteRenderer", rendererMock);
	}

	private void setupFitNesseRunnerMojo() throws Exception {
		runnerMojo = configureFitNesseMojo(new FitnesseRunnerMojo(), "run");
	}

	private void setupFitNesseStarterMojo() throws Exception {
		starterMojo = configureFitNesseMojo(new FitnesseStarterMojo(), "starter");
		setVariableValueToObject(starterMojo, "dependencies", createDependencies());
		setVariableValueToObject(starterMojo, "baseDir", new File(REPO));
		setVariableValueToObject(starterMojo, "jvmArguments", new String[0]);
		final Dependency velocity = new Dependency();
		velocity.setGroupId("org.apache.velocity");
		velocity.setArtifactId("velocity");
		velocity.setVersion("1.7");
		final Dependency[] jvmDependencies = new Dependency[1];
		jvmDependencies[0] = velocity;
		setVariableValueToObject(starterMojo, "jvmDependencies", jvmDependencies);

	}

	private void setupFitNesseContentMojo() throws Exception, IllegalAccessException {
		contentMojo = configureFitNesseMojo(new FitnesseContentMojo(), "content");
		final List<String> compileClasspathElements = createCompileClasspathElements();
		setVariableValueToObject(contentMojo, "compileClasspathElements", compileClasspathElements);
		setVariableValueToObject(contentMojo, "baseDir", new File(REPO));
		setVariableValueToObject(contentMojo, "wikiRoot", getTestDirectory() + TARGET);
		setVariableValueToObject(contentMojo, "nameRootPage", FITNESSE_ROOT);
	}

	private List<String> createCompileClasspathElements() {
		final List<String> compileSourceRoots = new ArrayList<String>();
		compileSourceRoots.add(REPO + "/" + JUNIT_JAR);
		compileSourceRoots.add(REPO + "/" + LOG4J_JAR);
		compileSourceRoots.add(REPO + "/" + FITNESSE_JAR);
		compileSourceRoots.add(getTestDirectory() + TARGET_CLASSES);
		return compileSourceRoots;
	}

	private List<Dependency> createDependencies() {
		final List<Dependency> dependencies = new ArrayList<Dependency>();
		final Dependency fitnesse = new Dependency();
		fitnesse.setArtifactId("fitnesse");
		fitnesse.setGroupId("org.fitnesse");
		fitnesse.setVersion("20111025");
		dependencies.add(fitnesse);
		return dependencies;
	}
}
