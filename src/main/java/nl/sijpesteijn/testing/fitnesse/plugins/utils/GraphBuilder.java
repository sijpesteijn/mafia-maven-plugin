package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import org.apache.maven.shared.dependency.graph.DependencyNode;

import java.util.List;

/**
 * GraphBuilder interface.
 */
public interface GraphBuilder {

    /**
     * Get the dependencyNodes in a certain scope.
     *
     * @param scope - SCOPE_TEST, SCOPE_COMPILE, etc
     * @return {@link java.util.List} of {@link DependencyNode}'s.
     * @throws MafiaException thrown in cause of an error.
     */
    List<DependencyNode> getDependencyNodes(final String scope) throws MafiaException;
}
