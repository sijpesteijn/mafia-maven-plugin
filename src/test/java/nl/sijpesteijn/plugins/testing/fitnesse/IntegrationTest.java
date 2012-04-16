package nl.sijpesteijn.plugins.testing.fitnesse;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.File;
import java.io.Writer;

import nl.sijpesteijn.testing.fitnesse.plugins.FitNesseReportMojo;
import nl.sijpesteijn.testing.fitnesse.plugins.FitNesseRunnerMojo;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.doxia.siterenderer.SiteRenderingContext;
import org.apache.maven.doxia.siterenderer.sink.SiteRendererSink;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.junit.Test;

public class IntegrationTest extends AbstractFitNesseTestCase {

    private FitNesseRunnerMojo runnerMojo;
    private FitNesseReportMojo reportMojo;
    private Renderer rendererMock;

    @Test
    public void testSetupPluginIntegration() throws Exception {
        setupFitNesseRunnerMojo();
        setupFitNesseReportMojo();

        createDummySuite();
        createDummyTest("");
        createDummyTest("1");
        // runnerMojo.execute();

        replay(rendererMock);
        reportMojo.execute();
        verify(rendererMock);

        final Sink value = (Sink) this.getVariableValueFromObject(reportMojo, "sink");
        final Writer writer = (Writer) this.getVariableValueFromObject(value, "writer");
        System.out.println(writer.toString());

    }

    private void setupFitNesseReportMojo() throws Exception {
        reportMojo = new FitNesseReportMojo();
        final Xpp3Dom configuration = getPluginConfiguration("mafia-maven-plugin", "collect-report");
        rendererMock = createMock(Renderer.class);
        rendererMock.generateDocument(isA(Writer.class), isA(SiteRendererSink.class), isA(SiteRenderingContext.class));
        expectLastCall();
        setVariableValueToObject(reportMojo, "siteRenderer", rendererMock);
        setVariableValueToObject(
                reportMojo,
                "outputDirectory",
                new File(getStringValueFromConfiguration(configuration, "outputDirectory",
                        "${project.build.outputDirectory}")));
        setVariableValueToObject(
                reportMojo,
                "mafiaTestResultsDirectory",
                getStringValueFromConfiguration(configuration, "mafiaTestResultsDirectory",
                        "${basedir}/target/FitNesseRoot/files/mafiaTestResults"));
        setVariableValueToObject(reportMojo, "suites", getStringArrayFromConfiguration(configuration, "suites"));
        setVariableValueToObject(reportMojo, "tests", getStringArrayFromConfiguration(configuration, "tests"));
    }

    private void setupFitNesseRunnerMojo() throws Exception {
        runnerMojo = new FitNesseRunnerMojo();
        final Xpp3Dom configuration = getPluginConfiguration("mafia-maven-plugin", "test");
        setVariableValueToObject(runnerMojo, "port",
                Integer.valueOf(getStringValueFromConfiguration(configuration, "port", "9091")));
        setVariableValueToObject(runnerMojo, "wikiRoot",
                getStringValueFromConfiguration(configuration, "wikiRoot", "${project.build.outputDirectory}"));
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

}
