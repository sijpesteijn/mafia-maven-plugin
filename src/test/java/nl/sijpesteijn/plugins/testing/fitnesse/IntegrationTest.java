package nl.sijpesteijn.plugins.testing.fitnesse;

import java.io.File;
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

import org.apache.maven.model.Dependency;
import org.junit.Test;

public class IntegrationTest extends AbstractFitNesseTestCase {

	private FitnesseContentMojo contentMojo;
	private FitnesseStarterMojo starterMojo;
	private FitnesseRunnerMojo runnerMojo;
	private FitnesseReportMojo reportMojo;
	private FitnesseStopperMojo stopperMojo;

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
		contentMojo.execute();
		createDummyTestsAndSuites();
		runnerMojo.execute();
		reportMojo.execute();
		// stopperMojo.execute();
	}

	private void createDummyTestsAndSuites() throws IOException {
		final StringBuilder dummytest = new StringBuilder();
		dummytest.append("|should I buy milk|\n")
				.append("|cash in wallet|credit card|pints of milk remaining|go to store?|\n")
				.append("|      0       |    no     |      0                |    no      |\n")
				.append("|      10      |    no     |      0                |    yes     |\n");
		final String nameRootPage = getTestDirectory() + TARGET + File.separatorChar + FITNESSE_ROOT;
		final File setupDir = new File(nameRootPage + "/FrontPage/BuyMilk/");
		setupDir.mkdirs();
		final File contentTxtFile = new File(nameRootPage + "/FrontPage/BuyMilk/" + "content.txt");
		final FileWriter contentFileWriter = new FileWriter(contentTxtFile);
		contentFileWriter.write(dummytest.toString());
		contentFileWriter.close();
		final File propertiesXmlFile = new File(nameRootPage + "/FrontPage/BuyMilk/" + "properties.xml");
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
	}

	private void setupFitNesseRunnerMojo() throws Exception {
		runnerMojo = configureFitNesseMojo(new FitnesseRunnerMojo(), "run");
	}

	private void setupFitNesseStarterMojo() throws Exception {
		starterMojo = configureFitNesseMojo(new FitnesseStarterMojo(), "starter");
		setVariableValueToObject(starterMojo, "dependencies", createDependencies());
		setVariableValueToObject(starterMojo, "baseDir", REPO);
		// setVariableValueToObject(starterMojo, "log", getTestDirectory() +
		// "/log/");
		setVariableValueToObject(starterMojo, "jvmArguments", new String[0]);
		setVariableValueToObject(starterMojo, "jvmDependencies", new Dependency[0]);

	}

	private void setupFitNesseContentMojo() throws Exception, IllegalAccessException {
		contentMojo = configureFitNesseMojo(new FitnesseContentMojo(), "content");
		final List<String> compileClasspathElements = createCompileClasspathElements();
		setVariableValueToObject(contentMojo, "compileClasspathElements", compileClasspathElements);
		setVariableValueToObject(contentMojo, "baseDir", REPO);
		setVariableValueToObject(contentMojo, "wikiRoot", getTestDirectory() + TARGET);
		setVariableValueToObject(contentMojo, "nameRootPage", FITNESSE_ROOT);
	}

	private List<String> createCompileClasspathElements() {
		final List<String> compileSourceRoots = new ArrayList<String>();
		compileSourceRoots.add(REPO + JUNIT_JAR);
		compileSourceRoots.add(REPO + LOG4J_JAR);
		compileSourceRoots.add(REPO + FITNESSE_JAR);
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
