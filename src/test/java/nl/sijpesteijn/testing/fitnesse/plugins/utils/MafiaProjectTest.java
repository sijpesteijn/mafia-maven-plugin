package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.repository.RepositorySystem;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: gijs
 * Date: 5/19/13 4:36 PM
 */
public class MafiaProjectTest {
    private MafiaProject mafiaProject;
    private MavenProject mavenProjectStub;
    private RepositorySystem repositorySystemMock;
    private ArtifactRepository artifactRepositoryMock;
    private MavenProjectBuilder mavenProjectBuilderMock;
    private final String SEP = File.separator;
    private Set<Artifact> artifacts;
    private List<Dependency> dependencies;
    private Artifact artifactStub;

    @Before
    public void setup() throws Throwable {
        mavenProjectStub = mock(MavenProject.class);
        artifactRepositoryMock = mock(ArtifactRepository.class);
        repositorySystemMock = mock(RepositorySystem.class);
        mavenProjectBuilderMock = mock(MavenProjectBuilder.class);

        artifactStub = getArtifactStub();
        artifacts = new HashSet<Artifact>();
        artifacts.add(artifactStub);

        dependencies = new ArrayList<Dependency>();
        dependencies.add(new Dependency());
        List<ArtifactRepository> remoteRepositories = new ArrayList<ArtifactRepository>();

        mafiaProject = new MafiaProject(mavenProjectStub, artifactRepositoryMock, repositorySystemMock,mavenProjectBuilderMock, remoteRepositories);
    }

    @Test
    public void testGetArtifacts() throws Throwable {
        when(mavenProjectStub.getArtifacts()).thenReturn(artifacts);
        Set<Artifact> artifacts = mafiaProject.getArtifacts();
        assertNotNull(artifacts);
        assertEquals("some", artifacts.iterator().next().getArtifactId());
    }

    @Test
    public void testCreateArtifact() throws Exception {
        Artifact input = getArtifactStub();
        when(repositorySystemMock.createArtifactWithClassifier(isA(String.class), isA(String.class), isA(String.class),
                isA(String.class), isA(String.class))).thenReturn(input);
        Artifact artifact = mafiaProject.createArtifact("org.sample", "some", "1.0", "test", "");

        assertEquals("org.sample", artifact.getGroupId());
        assertEquals("some", artifact.getArtifactId());
        assertEquals("1.0", artifact.getBaseVersion());
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
        when(artifactRepositoryMock.getBasedir()).thenReturn("." + File.separator + "target");
        Artifact artifact = mock(Artifact.class);
        when(artifact.getGroupId()).thenReturn("org.sample");
        when(artifact.getArtifactId()).thenReturn("some");
        when(artifact.getBaseVersion()).thenReturn("1.0");
        when(artifact.getScope()).thenReturn("test");
        when(artifact.getClassifier()).thenReturn("sources");

        String path = mafiaProject.resolveArtifact(artifact);
        assertNotNull(path);
        String root = "." + SEP + "target" + SEP + "org" + SEP + "sample" + SEP + "some" + SEP + "1.0" + SEP;
        assertEquals(root + "some-1.0-sources.jar", path);

    }

    @Test
    public void testResolveDependency() throws Exception {
        Dependency dependency = new Dependency();
        dependency.setGroupId("org.sample");
        dependency.setArtifactId("some");
        dependency.setVersion("1.0");
        dependency.setScope("test");

        when(artifactRepositoryMock.getBasedir()).thenReturn("." + SEP + "target");
        String path = mafiaProject.resolveDependency(dependency);
        assertNotNull(path);
        String root = "." + SEP + "target" + SEP + "org" + SEP + "sample" + SEP + "some" + SEP + "1.0" + SEP;
        assertEquals(root + "some-1.0.jar", path);
    }

//    @Test
//    public void testPluginDependencies() throws Throwable {
//        Artifact input = getDummyArtifact();
//
//        when(repositorySystemMock.createArtifactWithClassifier(isA(String.class), isA(String.class), isA(String.class),
//                isA(String.class), isA(String.class))).thenReturn(input);
//
////        when(mavenProjectStub.getDependencies()).thenReturn(new ArrayList<Dependency>());
//        when(mavenProjectBuilderMock.buildFromRepository(isA(Artifact.class), isA(List.class),
//                isA(ArtifactRepository.class))).thenReturn(mavenProjectStub);
//
//        List<Dependency> pluginDependencies = mafiaProject.getPluginDependencies();
//        assertNotNull(pluginDependencies);
//    }

//    @Test
//    public void testPluginDependenciesFailure() throws Throwable {
//        Artifact input = getDummyArtifact();
//        when(repositorySystemMock.createArtifactWithClassifier(isA(String.class), isA(String.class), isA(String.class),
//                isA(String.class), isA(String.class))).thenReturn(input);
//        when(mavenProjectBuilderMock.buildFromRepository(isA(Artifact.class), isA(List.class), isA(ArtifactRepository.class))).thenThrow(ProjectBuildingException.class);
//        try {
//            mafiaProject.getPluginDependencies();
//        } catch(MafiaException me) {
//            assertEquals("Could not load plugin artifact pom.", me.getMessage());
//        }
//    }

    @Test
    public void getPluginDependencies() throws Exception {
        when(mavenProjectStub.getPluginArtifacts()).thenReturn(artifacts);

        when(repositorySystemMock.createArtifactWithClassifier(artifactStub.getGroupId(),
                artifactStub.getArtifactId(), artifactStub.getVersion(), "jar",
                artifactStub.getClassifier())).thenReturn(artifactStub);
        MavenProject artifactProjectStub = mock(MavenProject.class);
        when(mavenProjectBuilderMock.buildFromRepository(isA(Artifact.class), isA(List.class),
                isA(ArtifactRepository.class))).thenReturn(artifactProjectStub);
        when(artifactProjectStub.getDependencies()).thenReturn(dependencies);

        List<Dependency> pluginDependencies = mafiaProject.getPluginDependencies();
        assertTrue(pluginDependencies.size() == 1);
    }

    @Test
    public void testPluginDependenciesFailure() throws Throwable {
        when(mavenProjectStub.getPluginArtifacts()).thenReturn(artifacts);

        when(repositorySystemMock.createArtifactWithClassifier(artifactStub.getGroupId(),
                artifactStub.getArtifactId(), artifactStub.getVersion(), "jar",
                artifactStub.getClassifier())).thenReturn(artifactStub);
        when(mavenProjectBuilderMock.buildFromRepository(isA(Artifact.class), isA(List.class),
                isA(ArtifactRepository.class))).thenThrow(ProjectBuildingException.class);
        try {
            mafiaProject.getPluginDependencies();
        } catch(MafiaException me) {
            assertEquals("Could not load plugin artifact pom.", me.getMessage());
        }
    }

    @Test
    public void getDependencies() throws Exception {
        when(mavenProjectStub.getDependencies()).thenReturn(dependencies);

        assertTrue(mafiaProject.getDependencies().size() == 1);
    }

    public Artifact getArtifactStub() {
        Artifact artifact = mock(Artifact.class);
        when(artifact.getGroupId()).thenReturn("org.sample");
        when(artifact.getArtifactId()).thenReturn("some");
        when(artifact.getBaseVersion()).thenReturn("1.0");
        when(artifact.getScope()).thenReturn("test");
        when(artifact.getClassifier()).thenReturn("sources");
        return artifact;
    }
}
