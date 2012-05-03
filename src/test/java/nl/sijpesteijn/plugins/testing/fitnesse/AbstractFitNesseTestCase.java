package nl.sijpesteijn.plugins.testing.fitnesse;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import nl.sijpesteijn.testing.fitnesse.plugins.FitNesseContentMojo;
import nl.sijpesteijn.testing.fitnesse.plugins.FitNesseReportMojo;
import nl.sijpesteijn.testing.fitnesse.plugins.FitNesseRunnerMojo;
import nl.sijpesteijn.testing.fitnesse.plugins.FitNesseStarterMojo;
import nl.sijpesteijn.testing.fitnesse.plugins.FitNesseStopperMojo;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.DependencyResolver;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.MavenUtils;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.SpecialPages;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.doxia.siterenderer.RendererException;
import org.apache.maven.doxia.siterenderer.SiteRenderingContext;
import org.apache.maven.doxia.siterenderer.sink.SiteRendererSink;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.ReflectionUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.junit.Before;

public abstract class AbstractFitNesseTestCase {

    protected static final String FITNESSE_ROOT = "FitNesseRoot";
    protected static final String MAFIA_TEST_RESULTS = "mafiaTestResults";
    protected static final String LOG4J_JAR = "log4j/log4j/1.2.15/log4j-1.2.15.jar";
    protected static final String JUNIT_JAR = "junit/junit/4.8.2/junit-4.8.2.jar";
    protected static final String FITNESSE_JAR = "org/fitnesse/fitnesse/20111025/fitnesse-20111025.jar";
    protected static final String TARGET_CLASSES = "target/classes";
    protected static final String TARGET = "/target";

    private String testDirectory;
    protected String REPO;
    protected Model model;
    protected Renderer rendererMock;
    private MavenUtils mavenUtils;

    @Before
    public void setUp() throws Exception {
        testDirectory = new File("").getAbsolutePath().replace("\\", "/");
        REPO = getRepositoryDirectory();
        final File pom = new File(testDirectory, "pom.xml");
        final File testPom = new File(testDirectory, "/src/test/resources/test-pom.xml");
        final MavenXpp3Reader reader = new MavenXpp3Reader();
        final MavenXpp3Reader testReader = new MavenXpp3Reader();
        model = reader.read(new InputStreamReader(new FileInputStream(pom)), true);
        final Model testModel = testReader.read(new InputStreamReader(new FileInputStream(testPom)), true);
        model.setBuild(testModel.getBuild());
        final Properties properties = model.getProperties();
        properties.put("basedir", testDirectory);
        properties.put("project.build.directory", testDirectory + "/target");
        properties.put("project.build.outputDirectory", testDirectory + "/target");
        properties.put("project.reporting.outputDirectory", testDirectory + "/target/site");
        mavenUtils = new MavenUtils(new MavenProject(model));
    }

    public String getTestDirectory() {
        return testDirectory;
    }

    public String getRepositoryDirectory() {
        String repoDir = System.getenv("MAVEN_REPO");
        if (repoDir != null) {
            return repoDir.replace('\\', '/');
        }

        repoDir = System.getProperty("user.home") + File.separatorChar + ".m2" + File.separatorChar + "repository";

        return repoDir.replace('\\', '/');

    }

    public Xpp3Dom getPluginConfiguration(final String artifactId, final String goal) throws MojoExecutionException {
        return mavenUtils.getPluginConfiguration("nl.sijpesteijn.testing.fitnesse.plugins", artifactId, goal);
    }

    protected List<String> getClasspathElements(final List<Dependency> dependencies) {
        final DependencyResolver resolver = new DependencyResolver();
        final List<String> classpathElements = new ArrayList<String>();
        for (final Dependency dependency : dependencies) {
            classpathElements.add(resolver.resolveDependencyPath(dependency, getRepositoryDirectory()));
        }
        return classpathElements;
    }

    protected Object getVariableValueFromObject(final Object object, final String variable)
            throws MojoExecutionException
    {
        final Field field = ReflectionUtils.getFieldByNameIncludingSuperclasses(variable, object.getClass());

        field.setAccessible(true);

        try {
            return field.get(object);
        } catch (final IllegalArgumentException e) {
            throw new MojoExecutionException(e.getMessage());
        } catch (final IllegalAccessException e) {
            throw new MojoExecutionException(e.getMessage());
        }
    }

    /**
     * Convenience method to set values to variables in objects that don't have setters
     * 
     * @param object
     * @param variable
     * @param value
     * @throws IllegalAccessException
     */
    protected void setVariableValueToObject(final Object object, final String variable, final Object value)
            throws MojoExecutionException
    {
        final Field field = ReflectionUtils.getFieldByNameIncludingSuperclasses(variable, object.getClass());

        field.setAccessible(true);

        try {
            field.set(object, value);
        } catch (final IllegalArgumentException e) {
            throw new MojoExecutionException(e.getMessage());
        } catch (final IllegalAccessException e) {
            throw new MojoExecutionException(e.getMessage());
        }
    }

    /**
     * Convenience method to obtain all variables and values from the mojo (including its superclasses)
     * 
     * Note: the values in the map are of type Object so the caller is responsible for casting to desired types.
     * 
     * @param object
     * @return map of variable names and values
     */
    @SuppressWarnings("rawtypes")
    protected Map getVariablesAndValuesFromObject(final Object object) throws MojoExecutionException {
        return getVariablesAndValuesFromObject(object.getClass(), object);
    }

    /**
     * Convenience method to obtain all variables and values from the mojo (including its superclasses)
     * 
     * Note: the values in the map are of type Object so the caller is responsible for casting to desired types.
     * 
     * @param clazz
     * @param object
     * @return map of variable names and values
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Map getVariablesAndValuesFromObject(final Class clazz, final Object object) throws MojoExecutionException
    {
        final Map map = new HashMap();

        final Field[] fields = clazz.getDeclaredFields();

        AccessibleObject.setAccessible(fields, true);

        for (int i = 0; i < fields.length; ++i) {
            final Field field = fields[i];

            try {
                map.put(field.getName(), field.get(object));
            } catch (final IllegalArgumentException e) {
                throw new MojoExecutionException(e.getMessage());
            } catch (final IllegalAccessException e) {
                throw new MojoExecutionException(e.getMessage());
            }

        }

        final Class superclass = clazz.getSuperclass();

        if (!Object.class.equals(superclass)) {
            map.putAll(getVariablesAndValuesFromObject(superclass, object));
        }

        return map;
    }

    protected FitNesseStopperMojo configureStopperMojo() throws MojoExecutionException {
        final FitNesseStopperMojo stopperMojo = new FitNesseStopperMojo();
        final Xpp3Dom configuration = getPluginConfiguration("mafia-maven-plugin", "stop");
        setVariableValueToObject(stopperMojo, "repositoryDirectory", REPO);
        setVariableValueToObject(stopperMojo, "port",
            Integer.valueOf(mavenUtils.getStringValueFromConfiguration(configuration, "port", "9090")));
        setVariableValueToObject(stopperMojo, "dependencies", model.getDependencies());
        return stopperMojo;
    }

    protected FitNesseStarterMojo configureStarterMojo() throws MojoExecutionException {
        final FitNesseStarterMojo starterMojo = new FitNesseStarterMojo();
        final Xpp3Dom configuration = getPluginConfiguration("mafia-maven-plugin", "start");
        setVariableValueToObject(starterMojo, "dependencies", model.getDependencies());
        setVariableValueToObject(starterMojo, "repositoryDirectory", REPO);
        setVariableValueToObject(starterMojo, "jvmArguments", new String[0]);
        setVariableValueToObject(starterMojo, "jvmDependencies",
            mavenUtils.getDependencyArrayFromConfiguration(configuration, "jvmDependencies"));
        setVariableValueToObject(starterMojo, "dependencies", model.getDependencies());
        setVariableValueToObject(starterMojo, "port",
            mavenUtils.getStringValueFromConfiguration(configuration, "port", "9090"));
        setVariableValueToObject(starterMojo, "retainDays",
            mavenUtils.getStringValueFromConfiguration(configuration, "retainDays", "14"));
        setVariableValueToObject(starterMojo, "wikiRoot",
            mavenUtils.getStringValueFromConfiguration(configuration, "wikiRoot", "${project.build.outputDirectory}"));
        setVariableValueToObject(starterMojo, "nameRootPage",
            mavenUtils.getStringValueFromConfiguration(configuration, "nameRootPage", FITNESSE_ROOT));
        return starterMojo;
    }

    protected FitNesseContentMojo configureContentMojo() throws MojoExecutionException {
        final Xpp3Dom configuration = getPluginConfiguration("mafia-maven-plugin", "content");
        final FitNesseContentMojo contentMojo = new FitNesseContentMojo();
        setVariableValueToObject(contentMojo, "statics",
            mavenUtils.getStringArrayFromConfiguration(configuration, "statics"));
        setVariableValueToObject(contentMojo, "resources",
            mavenUtils.getStringArrayFromConfiguration(configuration, "resources"));
        setVariableValueToObject(contentMojo, "targets",
            mavenUtils.getStringArrayFromConfiguration(configuration, "targets"));
        setVariableValueToObject(contentMojo, "excludeDependencies",
            mavenUtils.getDependencyArrayFromConfiguration(configuration, "excludeDependencies"));
        setVariableValueToObject(contentMojo, "compileClasspathElements", getClasspathElements(model.getDependencies()));
        setVariableValueToObject(contentMojo, "repositoryDirectory", REPO);
        setVariableValueToObject(contentMojo, "wikiRoot", getTestDirectory() + TARGET);
        setVariableValueToObject(contentMojo, "nameRootPage", FITNESSE_ROOT);
        return contentMojo;
    }

    protected FitNesseRunnerMojo configureRunnerMojo() throws MojoExecutionException {
        final FitNesseRunnerMojo runnerMojo = new FitNesseRunnerMojo();
        final Xpp3Dom configuration = getPluginConfiguration("mafia-maven-plugin", "test");
        setVariableValueToObject(runnerMojo, "port",
            Integer.valueOf(mavenUtils.getStringValueFromConfiguration(configuration, "port", "9091")));
        setVariableValueToObject(runnerMojo, "wikiRoot",
            mavenUtils.getStringValueFromConfiguration(configuration, "wikiRoot", "${project.build.outputDirectory}"));
        setVariableValueToObject(runnerMojo, "nameRootPage",
            mavenUtils.getStringValueFromConfiguration(configuration, "nameRootPage", FITNESSE_ROOT));
        setVariableValueToObject(runnerMojo, "mafiaTestResultsDirectory",
            mavenUtils.getStringValueFromConfiguration(configuration, "mafiaTestResultsDirectory", MAFIA_TEST_RESULTS));
        setVariableValueToObject(runnerMojo, "repositoryDirectory", REPO);
        setVariableValueToObject(runnerMojo, "tests",
            mavenUtils.getStringArrayFromConfiguration(configuration, "tests"));
        setVariableValueToObject(runnerMojo, "suites",
            mavenUtils.getStringArrayFromConfiguration(configuration, "suites"));
        setVariableValueToObject(runnerMojo, "suitePageName",
            mavenUtils.getStringValueFromConfiguration(configuration, "suitePageName", null));
        setVariableValueToObject(runnerMojo, "suiteFilter",
            mavenUtils.getStringValueFromConfiguration(configuration, "suiteFilter", null));
        setVariableValueToObject(runnerMojo, "stopTestsOnFailure", true);
        setVariableValueToObject(runnerMojo, "stopTestsOnIgnore", true);
        setVariableValueToObject(runnerMojo, "stopTestsOnException", true);
        setVariableValueToObject(runnerMojo, "stopTestsOnWrong", true);
        return runnerMojo;
    }

    protected FitNesseReportMojo configureReporterMojo() throws MojoExecutionException {
        final FitNesseReportMojo reporterMojo = new FitNesseReportMojo();
        final Xpp3Dom configuration = getPluginConfiguration("mafia-maven-plugin", "report");
        final Xpp3Dom testConfiguration = getPluginConfiguration("mafia-maven-plugin", "test");
        rendererMock = createMock(Renderer.class);
        try {
            rendererMock.generateDocument(isA(Writer.class), isA(SiteRendererSink.class),
                isA(SiteRenderingContext.class));
        } catch (final RendererException e) {
            throw new MojoExecutionException(e.getMessage());
        }
        expectLastCall();
        setVariableValueToObject(reporterMojo, "siteRenderer", rendererMock);
        setVariableValueToObject(
            reporterMojo,
            "outputDirectory",
            new File(mavenUtils.getStringValueFromConfiguration(configuration, "outputDirectory",
                "${project.reporting.outputDirectory}")));
        setVariableValueToObject(reporterMojo, "mafiaTestResultsDirectory",
            mavenUtils.getStringValueFromConfiguration(configuration, "mafiaTestResultsDirectory", MAFIA_TEST_RESULTS));
        setVariableValueToObject(reporterMojo, "repositoryDirectory", REPO);
        setVariableValueToObject(reporterMojo, "runTests",
            Boolean.valueOf(mavenUtils.getStringValueFromConfiguration(testConfiguration, "runTests", "true")));
        setVariableValueToObject(reporterMojo, "wikiRoot",
                mavenUtils.getStringValueFromConfiguration(configuration, "wikiRoot", "${project.build.outputDirectory}"));
        setVariableValueToObject(reporterMojo, "project", new MavenProject());
        setVariableValueToObject(reporterMojo, "suites",
            mavenUtils.getStringArrayFromConfiguration(testConfiguration, "suites"));
        setVariableValueToObject(reporterMojo, "tests",
            mavenUtils.getStringArrayFromConfiguration(testConfiguration, "tests"));
        setVariableValueToObject(reporterMojo, "suitePageName",
            mavenUtils.getStringValueFromConfiguration(testConfiguration, "suitePageName", null));
        setVariableValueToObject(reporterMojo, "suiteFilter",
            mavenUtils.getStringValueFromConfiguration(testConfiguration, "suiteFilter", null));
        return reporterMojo;
    }

    protected void deleteTestDirectory() throws IOException {
        FileUtils.deleteDirectory(getTestDirectory() + "/target/" + FITNESSE_ROOT);
    }

    protected void createDummySuite() throws IOException {
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

    protected void createDummyTest(final String sub) throws IOException {
        final StringBuilder dummytest = new StringBuilder();
        dummytest.append("|should I buy milk|\n")
            .append("|cash in wallet|credit card|pints of milk remaining|go to store?|\n")
            .append("|      0       |    no     |      0                |    no      |\n")
            .append("|      10      |    no     |      0                |    yes     |\n");
        final String nameRootPage = getTestDirectory() + TARGET + File.separatorChar + FITNESSE_ROOT;
        final File setupDir = new File(nameRootPage + "/FrontPage/BuyMilkSuite/BuyMilk" + sub + "/");
        setupDir.mkdirs();
        final File contentTxtFile =
                new File(nameRootPage + "/FrontPage/BuyMilkSuite/BuyMilk" + sub + "/" + "content.txt");
        final FileWriter contentFileWriter = new FileWriter(contentTxtFile);
        contentFileWriter.write(dummytest.toString());
        contentFileWriter.close();
        final File propertiesXmlFile =
                new File(nameRootPage + "/FrontPage/BuyMilkSuite/BuyMilk" + sub + "/" + "properties.xml");
        final FileWriter propertiesFileWriter = new FileWriter(propertiesXmlFile);
        propertiesFileWriter.write(SpecialPages.PropertiesXml.replace("</properties>",
            "<Test/><Suites>regression</Suites></properties>"));
        propertiesFileWriter.close();
    }

}
