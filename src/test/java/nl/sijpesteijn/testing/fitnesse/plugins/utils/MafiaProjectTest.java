package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.repository.RepositorySystem;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: gijs
 * Date: 5/19/13 4:36 PM
 */
public class MafiaProjectTest {
    private MafiaProject mafiaProject;
    private MavenProject mavenProject;
    private RepositorySystem repositorySystemMock;
    private ArtifactRepository artifactRepositoryMock;
    private MavenProjectBuilder mavenProjectBuilderMock;

    @Before
    public void setup() throws Throwable {
        mavenProject = new MavenProject();
        Set<Artifact> pluginArtifacts = new HashSet<Artifact>();
        pluginArtifacts.add(getDummyArtifact());
        mavenProject.setArtifacts(pluginArtifacts);
        mavenProject.setPluginArtifacts(pluginArtifacts);
        artifactRepositoryMock = mock(ArtifactRepository.class);
        repositorySystemMock = mock(RepositorySystem.class);
        mavenProjectBuilderMock = mock(MavenProjectBuilder.class);
        List<ArtifactRepository> remoteRepositories = new ArrayList<ArtifactRepository>();
        mafiaProject = new MafiaProject(mavenProject, artifactRepositoryMock, repositorySystemMock,mavenProjectBuilderMock, remoteRepositories);
    }

    @Test
    public void testGetArtifacts() throws Throwable {
        Set<Artifact> artifacts = mafiaProject.getArtifacts();
        assertNotNull(artifacts);
        assertEquals("some", artifacts.iterator().next().getArtifactId());
    }

    @Test
    public void testCreateArtifact() throws Exception {
        Artifact input = getDummyArtifact();
        when(repositorySystemMock.createArtifact(isA(String.class), isA(String.class), isA(String.class), isA(String.class), isA(String.class))).thenReturn(input);
        Artifact artifact = mafiaProject.createArtifact("org.sample", "some", "1.0", "test");

        assertEquals("org.sample", artifact.getGroupId());
        assertEquals("some", artifact.getArtifactId());
        assertEquals("1.0", artifact.getVersion());
        assertEquals("test", artifact.getScope());
    }

    @Test
    public void testGetPluginArtifacts() throws Exception {
        Set<Artifact> pluginArtifacts = mafiaProject.getPluginArtifacts();
        assertNotNull(pluginArtifacts);
    }

    @Test
    public void testGetLocalRepository() throws Exception {
        ArtifactRepository localRespository = mafiaProject.getLocalRepository();
        assertNotNull(localRespository);
    }

    @Test
    public void testResolveArtifact() throws Exception {
        when(artifactRepositoryMock.getBasedir()).thenReturn("./target");
        Artifact artifact = mock(Artifact.class);
        when(artifact.getGroupId()).thenReturn("org.sample");
        when(artifact.getArtifactId()).thenReturn("some");
        when(artifact.getVersion()).thenReturn("1.0");
        when(artifact.getScope()).thenReturn("test");
        when(artifact.getClassifier()).thenReturn("sources");

        String path = mafiaProject.resolveArtifact(artifact);
        assertNotNull(path);
        assertEquals("./target/org/sample/some/1.0/some-1.0-sources.jar", path);

        Artifact dummyArtifact = getDummyArtifact();
        path = mafiaProject.resolveArtifact(dummyArtifact);
        assertNotNull(path);
        assertEquals("./target/org/sample/some/1.0/some-1.0.jar", path);
    }

    @Test
    public void testResolveDependency() throws Exception {
        Dependency dependency = new Dependency();
        dependency.setGroupId("org.sample");
        dependency.setArtifactId("some");
        dependency.setVersion("1.0");
        dependency.setScope("test");

        when(artifactRepositoryMock.getBasedir()).thenReturn("./target");
        String path = mafiaProject.resolveDependency(dependency);
        assertNotNull(path);
        assertEquals("./target/org/sample/some/1.0/some-1.0.jar", path);
    }

    @Test
    public void testPluginDependencies() throws Throwable {
        Artifact input = getDummyArtifact();
        when(repositorySystemMock.createArtifact(isA(String.class), isA(String.class), isA(String.class), isA(String.class), isA(String.class))).thenReturn(input);
        when(mavenProjectBuilderMock.buildFromRepository(isA(Artifact.class), isA(List.class), isA(ArtifactRepository.class))).thenReturn(new MavenProject());
        List<Dependency> pluginDependencies = mafiaProject.getPluginDependencies();

        assertNotNull(pluginDependencies);
    }

    @Test
    public void testPluginDependenciesFailure() throws Throwable {
        Artifact input = getDummyArtifact();
        when(repositorySystemMock.createArtifact(isA(String.class), isA(String.class), isA(String.class), isA(String.class), isA(String.class))).thenReturn(input);
        when(mavenProjectBuilderMock.buildFromRepository(isA(Artifact.class), isA(List.class), isA(ArtifactRepository.class))).thenThrow(ProjectBuildingException.class);
        try {
            mafiaProject.getPluginDependencies();
        } catch(MafiaException me) {
            assertEquals("Could not load plugin artifact pom.", me.getMessage());
        }
    }

    public Artifact getDummyArtifact() {
        Artifact artifact = new ArtifactStub();
        artifact.setGroupId("org.sample");
        artifact.setArtifactId("some");
        artifact.setVersion("1.0");
        artifact.setScope("test");
        ((ArtifactStub)artifact).setType("jar");
        return artifact;
    }
}
