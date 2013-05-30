package nl.sijpesteijn.testing.fitnesse.plugins.context;

import nl.sijpesteijn.testing.fitnesse.plugins.utils.Project;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: gijs
 * Date: 5/19/13 7:15 PM
 */
public class FitNesseContextWriterTest {
    private FitNesseContextWriter fitNesseContextWriter;
    private File contentDir;
    private Project projectMock;
    private List<String> statics = new ArrayList<String>() {{ add("!define TEST_SYSTEM {slim}"); }};
    private List<String> targets = new ArrayList<String>() {{ add("/web"); }};
    private List<String> resources = new ArrayList<String>() {{ add("/src/test/resources"); }};

    @Before
    public void setup() throws Throwable {
        contentDir = new File("./target/");
        projectMock = mock(Project.class);
    }

    @Test
    public void writeContent() throws Throwable {
        List<Dependency> excludeDependencies = getExcludeDependencies();
        fitNesseContextWriter = new FitNesseContextWriter(projectMock, statics, targets, resources, excludeDependencies, contentDir, true);
        Set<Artifact> artifacts = new HashSet<Artifact>();
        Artifact fitnesseArtifact = new ArtifactStub();
        fitnesseArtifact.setGroupId("org.fitnesse");
        fitnesseArtifact.setArtifactId("fitnesse");
        fitnesseArtifact.setVersion("1.0.0");
        artifacts.add(fitnesseArtifact);
        Artifact excludeArtifact = new ArtifactStub();
        excludeArtifact.setGroupId("org.exclude");
        excludeArtifact.setArtifactId("exclude");
        artifacts.add(excludeArtifact);

        when(projectMock.getArtifacts()).thenReturn(artifacts);
        when(projectMock.resolveArtifact(fitnesseArtifact)).thenReturn("fitnessePath");
        List<Dependency> pluginDependencies = new ArrayList<Dependency>();
        Dependency someDependency = new Dependency();
        someDependency.setGroupId("some.dependency");
        someDependency.setArtifactId("some-dependency");
        pluginDependencies.add(someDependency);
        when(projectMock.getPluginDependencies()).thenReturn(pluginDependencies);
        when(projectMock.resolveDependency(someDependency)).thenReturn("somePath");

        fitNesseContextWriter.writeContent();

        String content = FileUtils.readFileToString(new File(contentDir, "content.txt"));
        assertTrue(content.contains("!define TEST_SYSTEM {slim}"));
        assertTrue(content.contains("!path /src/test/resources"));
        assertTrue(content.contains("!path /web/target/classes/"));
        assertTrue(content.contains("!path fitnessePath"));
        assertTrue(content.contains("!path somePath"));
    }

    @Test
    public void testMinimalContext() throws Exception {
        fitNesseContextWriter = new FitNesseContextWriter(projectMock, null, null, null, null, contentDir, false);
        fitNesseContextWriter.writeContent();

        String content = FileUtils.readFileToString(new File(contentDir, "content.txt"));
        assertFalse(content.contains("!define TEST_SYSTEM {slim}"));
    }

    public List<Dependency> getExcludeDependencies() {
        List<Dependency> excludeDependencies = new ArrayList<Dependency>();
        Dependency excludeDependency = new Dependency();
        excludeDependency.setGroupId("org.exclude");
        excludeDependency.setArtifactId("exclude");
        excludeDependencies.add(excludeDependency);
        return excludeDependencies;
    }
}
