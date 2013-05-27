package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;

import java.util.List;

/**
 * MafiaDependencyNodeVisitor interface.
 */
public interface MafiaDependencyNodeVisitor extends DependencyNodeVisitor {

    /**
     * Get the nodes.
     *
     * @return {@link java.util.List} of {@link org.apache.maven.shared.dependency.graph.DependencyNode}
     */
    List<DependencyNode> getNodes();
}
