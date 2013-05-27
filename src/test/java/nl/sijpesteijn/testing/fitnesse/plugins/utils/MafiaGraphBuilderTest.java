package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.artifact.filter.ScopeArtifactFilter;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: gijs
 * Date: 5/19/13 5:12 PM
 */
public class MafiaGraphBuilderTest {
    private MafiaGraphBuilder mafiaGraphBuilder;
    private DependencyGraphBuilder dependencyGraphBuilderMock;
    private MavenProject mavenProject;
    private MafiaDependencyNodeVisitor visitorMock;

    @Before
    public void setup() throws Throwable {
        mavenProject = new MavenProject();
        dependencyGraphBuilderMock = mock(DependencyGraphBuilder.class);
        visitorMock = mock(MafiaDependencyNodeVisitor.class);
        mafiaGraphBuilder = new MafiaGraphBuilder(mavenProject, dependencyGraphBuilderMock, visitorMock);

    }

    @Test
    public void testNodes() throws Throwable {
        DependencyNode dependencyNodeMock = mock(DependencyNode.class);
        List<DependencyNode> dependencyNodes = new ArrayList<DependencyNode>();

        when(dependencyGraphBuilderMock.buildDependencyGraph(isA(MavenProject.class), isA(ScopeArtifactFilter.class))).thenReturn(dependencyNodeMock);
        when(visitorMock.getNodes()).thenReturn(dependencyNodes);

        List<DependencyNode> returnedNodes = mafiaGraphBuilder.getDependencyNodes(DefaultArtifact.SCOPE_TEST);

        assertNotNull(returnedNodes);
    }

    @Test
    public void testException() throws Exception {
        try {
            when(dependencyGraphBuilderMock.buildDependencyGraph(isA(MavenProject.class), isA(ScopeArtifactFilter.class))).thenThrow(DependencyGraphBuilderException.class);
            mafiaGraphBuilder.getDependencyNodes(DefaultArtifact.SCOPE_TEST);
        } catch (MafiaException e) {
            assertEquals("Could not get dependency nodes with scope: " + DefaultArtifact.SCOPE_TEST, e.getMessage());
        }
    }
}
