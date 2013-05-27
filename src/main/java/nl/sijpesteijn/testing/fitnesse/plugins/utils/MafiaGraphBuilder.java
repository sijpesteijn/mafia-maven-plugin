package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.artifact.filter.ScopeArtifactFilter;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;

import java.util.List;

/**
 * MafiaGraphBuilder.
 */
public class MafiaGraphBuilder implements GraphBuilder {

    /**
     * Maven project.
     */
    private MavenProject project;

    /**
     * Dependency graph builder.
     */
    private DependencyGraphBuilder dependencyGraphBuilder;

    /**
     * Mafia dependency visitor.
     */
    private MafiaDependencyNodeVisitor visitor;

    /**
     * Constructor.
     *
     * @param project                - the Maven project
     * @param dependencyGraphBuilder - the DependencyGraphBuilder
     * @param visitor                - the MafiaDependencyNodeVisitor
     */
    public MafiaGraphBuilder(final MavenProject project, final DependencyGraphBuilder dependencyGraphBuilder,
                             final MafiaDependencyNodeVisitor visitor) {
        this.project = project;
        this.dependencyGraphBuilder = dependencyGraphBuilder;
        this.visitor = visitor;
    }

    /**
     * {@inheritDoc}
     */
    public final List<DependencyNode> getDependencyNodes(final String scope) throws MafiaException {
        try {
            DependencyNode rootNode =
                    dependencyGraphBuilder.buildDependencyGraph(project, new ScopeArtifactFilter(scope));
            rootNode.accept(visitor);
            return visitor.getNodes();
        } catch (DependencyGraphBuilderException e) {
            throw new MafiaException("Could not get dependency nodes with scope: " + scope, e);
        }
    }

}
