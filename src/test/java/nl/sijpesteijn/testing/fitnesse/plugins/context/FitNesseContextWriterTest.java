package nl.sijpesteijn.testing.fitnesse.plugins.context;

import nl.sijpesteijn.testing.fitnesse.plugins.utils.Project;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
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
        contentDir = new File("./target/content/");
        FileUtils.deleteDirectory(contentDir);
        projectMock = mock(Project.class);
    }

    @Test
    public void writeContent() throws Throwable {
        fitNesseContextWriter = new FitNesseContextWriter(projectMock, statics, targets, resources, new ArrayList<Dependency>(),
                contentDir, true);
        Set<Artifact> artifacts = new HashSet<Artifact>();
        Artifact fitnesseArtifact = new ArtifactStub();
        fitnesseArtifact.setGroupId("org.fitnesse");
        fitnesseArtifact.setArtifactId("fitnesse");
        fitnesseArtifact.setVersion("1.0.0");
        artifacts.add(fitnesseArtifact);

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

    @Test
    public void testExcludeDependencies() throws Exception {
        Set<Artifact> artifacts = new HashSet<Artifact>();
        Artifact stubArtifact1 = mock(Artifact.class);
        when(stubArtifact1.getGroupId()).thenReturn("org.exclude");
        when(stubArtifact1.getArtifactId()).thenReturn("exclude");
        when(stubArtifact1.getVersion()).thenReturn("1.0.0");
        when(stubArtifact1.getType()).thenReturn("jar");
        when(stubArtifact1.getClassifier()).thenReturn("sources");
        when(stubArtifact1.getScope()).thenReturn("runtime");
        artifacts.add(stubArtifact1);
        Artifact stubArtifact2 = mock(Artifact.class);
        when(stubArtifact2.getGroupId()).thenReturn("org.exclude");
        when(stubArtifact2.getArtifactId()).thenReturn("exclude");
        when(stubArtifact2.getVersion()).thenReturn("1.0.0");
        when(stubArtifact2.getType()).thenReturn("jar");
        when(stubArtifact2.getClassifier()).thenReturn("sources");
        artifacts.add(stubArtifact2);
        Artifact stubArtifact3 = mock(Artifact.class);
        when(stubArtifact3.getGroupId()).thenReturn("org.exclude");
        when(stubArtifact3.getArtifactId()).thenReturn("exclude");
        when(stubArtifact3.getVersion()).thenReturn("1.0.0");
        when(stubArtifact3.getType()).thenReturn("jar");
        artifacts.add(stubArtifact3);
        Artifact stubArtifact4 = mock(Artifact.class);
        when(stubArtifact4.getGroupId()).thenReturn("org.exclude");
        when(stubArtifact4.getArtifactId()).thenReturn("exclude");
        when(stubArtifact4.getVersion()).thenReturn("1.0.0");
        artifacts.add(stubArtifact4);
        Artifact stubArtifact5 = mock(Artifact.class);
        when(stubArtifact5.getGroupId()).thenReturn("org.exclude");
        when(stubArtifact5.getArtifactId()).thenReturn("exclude");
        artifacts.add(stubArtifact5);
        Artifact stubArtifact6 = mock(Artifact.class);
        when(stubArtifact6.getGroupId()).thenReturn("org.exclude");
        artifacts.add(stubArtifact6);
        Artifact stubArtifact7 = mock(Artifact.class);
        artifacts.add(stubArtifact7);
        Artifact stubArtifact8 = mock(Artifact.class);
        when(stubArtifact8.getGroupId()).thenReturn("org.exclude");
        when(stubArtifact8.getArtifactId()).thenReturn("exclude");
        when(stubArtifact8.getVersion()).thenReturn("1.0.0");
        when(stubArtifact8.getType()).thenReturn("war");
        when(stubArtifact8.getClassifier()).thenReturn("sources");
        when(stubArtifact8.getScope()).thenReturn("runtime");
        artifacts.add(stubArtifact8);

        when(projectMock.getArtifacts()).thenReturn(artifacts);


        fitNesseContextWriter = new FitNesseContextWriter(projectMock, null, null, null, getExcludeDependencies(), contentDir, false);
        fitNesseContextWriter.writeContent();

        String content = FileUtils.readFileToString(new File(contentDir, "content.txt"));
        assertFalse(content.contains("!define TEST_SYSTEM {slim}"));
    }

    public List<Dependency> getExcludeDependencies() {
        List<Dependency> excludeDependencies = new ArrayList<Dependency>();
        Dependency dep1 = mock(Dependency.class);
        when(dep1.getGroupId()).thenReturn("org.exclude");
        when(dep1.getArtifactId()).thenReturn("exclude");
        when(dep1.getVersion()).thenReturn("1.0.0");
        when(dep1.getType()).thenReturn("jar");
        when(dep1.getClassifier()).thenReturn("sources");
        when(dep1.getScope()).thenReturn("runtime");
        excludeDependencies.add(dep1);
        return excludeDependencies;
    }
}
