package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.junit.Before;
import org.junit.Test;

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
 * Date: 5/19/13 11:49 AM
 */
public class FitNesseJarLocatorTest {
    private FitNesseJarLocator locator;
    private Project mafiaProjectMock;
    private Artifact fitnesseArtifact;

    @Before
    public void setup() throws Throwable {
        mafiaProjectMock = mock(Project.class);
        locator = new FitNesseJarLocator(mafiaProjectMock);
    }

    @Test(expected = MafiaException.class)
    public void testNotFound() throws Throwable {
        String fitNesseJarPath = locator.getFitNesseJarPath();
        assertEquals("Could not find any fitnesse dependency in the pom information. Did you add a FitNesse depedency?", fitNesseJarPath);
    }

    @Test
    public void testGetPath() throws Throwable {
        Set<Artifact> artifacts = new HashSet<Artifact>();
        Artifact someArtifact = new ArtifactStub();
        someArtifact.setArtifactId("some-artifact");
        fitnesseArtifact = new ArtifactStub();
        fitnesseArtifact.setArtifactId("mafia-maven-plugin");
        fitnesseArtifact.setGroupId("nl.sijpesteijn.testing.fitnesse.plugins");
        fitnesseArtifact.setVersion("1.0.0");
        artifacts.add(someArtifact);
        artifacts.add(fitnesseArtifact);

        when(mafiaProjectMock.getPluginArtifacts()).thenReturn(artifacts);
        Artifact pluginArtifact = new ArtifactStub();
        when(mafiaProjectMock.createArtifact(fitnesseArtifact.getGroupId(), fitnesseArtifact.getArtifactId(), fitnesseArtifact.getVersion(), fitnesseArtifact.getScope())).thenReturn(pluginArtifact);
        List<Dependency> dependencies = new ArrayList<Dependency>();
        Dependency fitNesseDependency = new Dependency();
        fitNesseDependency.setGroupId("org.fitnesse");
        fitNesseDependency.setArtifactId("fitnesse");
        fitNesseDependency.setVersion("1.0.0");
        dependencies.add(fitNesseDependency);
        Dependency someDependency = new Dependency();
        someDependency.setGroupId("org.artifact");
        someDependency.setArtifactId("some-artifact");
        someDependency.setVersion("2.0.0");
        dependencies.add(someDependency);
        when(mafiaProjectMock.getArtifactDependencies(pluginArtifact)).thenReturn(dependencies);
        when(mafiaProjectMock.resolveArtifact(isA(Artifact.class))).thenReturn("path to fitnesse");
        String path = locator.getFitNesseJarPath();


        assertNotNull(path);
        assertTrue("path to fitnesse".equals(path));
    }

    @Test
    public void testSearchArtifacts() throws Throwable {
        Set<Artifact> artifacts = new HashSet<Artifact>();
        Artifact someArtifact = new ArtifactStub();
        someArtifact.setArtifactId("some-artifact");
        someArtifact.setGroupId("some-groupid");
        fitnesseArtifact = new ArtifactStub();
        fitnesseArtifact.setArtifactId("fitnesse");
        fitnesseArtifact.setGroupId("org.fitnesse");
        fitnesseArtifact.setVersion("1.0.0");
        artifacts.add(someArtifact);
        artifacts.add(fitnesseArtifact);

        when(mafiaProjectMock.getArtifacts()).thenReturn(artifacts);
        locator.getFitNesseJarPath();
    }

}
