package nl.sijpesteijn.plugins.testing.fitnesse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import nl.sijpesteijn.testing.fitnesse.plugins.FitnesseContentMojo;
import nl.sijpesteijn.testing.fitnesse.plugins.FitnesseReportMojo;
import nl.sijpesteijn.testing.fitnesse.plugins.FitnesseRunnerMojo;
import nl.sijpesteijn.testing.fitnesse.plugins.FitnesseStarterMojo;
import nl.sijpesteijn.testing.fitnesse.plugins.FitnesseStopperMojo;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.SpecialPages;

import org.apache.maven.doxia.siterenderer.DefaultSiteRenderer;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
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
		stopperMojo.execute();
		contentMojo.execute();
		createDummySuite();
		createDummyTest("");
		createDummyTest("1");
		runnerMojo.execute();

		reportMojo.execute();

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
		stopperMojo = new FitnesseStopperMojo();
		final Xpp3Dom configuration = getPluginConfiguration("mafia-maven-plugin", "stop");
		setVariableValueToObject(stopperMojo, "baseDir", REPO);
		setVariableValueToObject(stopperMojo, "port", getStringValueFromConfiguration(configuration, "port", "9090"));
		setVariableValueToObject(stopperMojo, "dependencies", model.getDependencies());

	}

	private void setupFitNesseReportMojo() throws Exception {
		reportMojo = new FitnesseReportMojo();
		final Xpp3Dom configuration = getPluginConfiguration("mafia-maven-plugin", "collect-report");
		setVariableValueToObject(reportMojo, "siteRenderer", new DefaultSiteRenderer());
		setVariableValueToObject(
				reportMojo,
				"outputDirectory",
				new File(
						getStringValueFromConfiguration(configuration, "outputDirectory", "${basedir}/target/fitnesse")));
		setVariableValueToObject(
				reportMojo,
				"mafiaTestResultsDirectory",
				getStringValueFromConfiguration(configuration, "mafiaTestResultsDirectory",
						"${basedir}/target/FitNesseRoot/files/mafiaTestResults"));
		setVariableValueToObject(reportMojo, "suites", getStringArrayFromConfiguration(configuration, "suites"));
		setVariableValueToObject(reportMojo, "tests", getStringArrayFromConfiguration(configuration, "tests"));
		setVariableValueToObject(reportMojo, "buildDirectory",
				getStringValueFromConfiguration(configuration, "${project.build.directory}", "${basedir}/target"));
	}

	private void setupFitNesseRunnerMojo() throws Exception {
		runnerMojo = new FitnesseRunnerMojo();
		final Xpp3Dom configuration = getPluginConfiguration("mafia-maven-plugin", "run-tests");
		setVariableValueToObject(runnerMojo, "port", getStringValueFromConfiguration(configuration, "port", "9091"));
		setVariableValueToObject(runnerMojo, "wikiRoot",
				getStringValueFromConfiguration(configuration, "wikiRoot", "${basedir}/target"));
		setVariableValueToObject(runnerMojo, "nameRootPage",
				getStringValueFromConfiguration(configuration, "nameRootPage", "FitNesseRoot"));
		setVariableValueToObject(runnerMojo, "mafiaTestResultsDirectory",
				getStringValueFromConfiguration(configuration, "mafiaTestResultsDirectory", "mafiaTestResults"));
		setVariableValueToObject(runnerMojo, "tests", getStringArrayFromConfiguration(configuration, "tests"));
		setVariableValueToObject(runnerMojo, "suites", getStringArrayFromConfiguration(configuration, "suites"));
		setVariableValueToObject(runnerMojo, "stopTestsOnFailure", true);
		setVariableValueToObject(runnerMojo, "stopTestsOnIgnore", true);
		setVariableValueToObject(runnerMojo, "stopTestsOnException", true);
		setVariableValueToObject(runnerMojo, "stopTestsOnWrong", true);
	}

	private void setupFitNesseStarterMojo() throws Exception {
		starterMojo = new FitnesseStarterMojo();
		final Xpp3Dom configuration = getPluginConfiguration("mafia-maven-plugin", "start");
		setVariableValueToObject(starterMojo, "dependencies", model.getDependencies());
		setVariableValueToObject(starterMojo, "basedir", new File(REPO));
		setVariableValueToObject(starterMojo, "jvmArguments", new String[0]);
		setVariableValueToObject(starterMojo, "jvmDependencies",
				getDependencyArrayFromConfiguration(configuration, "jvmDependencies"));
		setVariableValueToObject(starterMojo, "dependencies", model.getDependencies());
		setVariableValueToObject(starterMojo, "port", getStringValueFromConfiguration(configuration, "port", "9090"));
		setVariableValueToObject(starterMojo, "retainDays",
				getStringValueFromConfiguration(configuration, "retainDays", "14"));
		setVariableValueToObject(starterMojo, "wikiRoot",
				getStringValueFromConfiguration(configuration, "wikiRoot", "${basedir}/target"));
		setVariableValueToObject(starterMojo, "nameRootPage",
				getStringValueFromConfiguration(configuration, "nameRootPage", "FitNesseRoot"));

	}

	private void setupFitNesseContentMojo() throws Exception, IllegalAccessException {
		contentMojo = new FitnesseContentMojo();
		final Xpp3Dom configuration = getPluginConfiguration("mafia-maven-plugin", "content");
		setVariableValueToObject(contentMojo, "statics", getStringArrayFromConfiguration(configuration, "statics"));
		setVariableValueToObject(contentMojo, "resources", getStringArrayFromConfiguration(configuration, "resources"));
		setVariableValueToObject(contentMojo, "targets", getStringArrayFromConfiguration(configuration, "targets"));
		setVariableValueToObject(contentMojo, "excludeDependencies",
				getDependencyArrayFromConfiguration(configuration, "excludeDependencies"));
		setVariableValueToObject(contentMojo, "compileClasspathElements", getClasspathElements(model.getDependencies()));
		setVariableValueToObject(contentMojo, "basedir", new File(REPO));
		setVariableValueToObject(contentMojo, "wikiRoot", getTestDirectory() + TARGET);
		setVariableValueToObject(contentMojo, "nameRootPage", FITNESSE_ROOT);
	}

}
