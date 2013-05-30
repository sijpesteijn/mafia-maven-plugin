package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;

import java.util.List;
import java.util.Set;

/**
 * Interface for {@link nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaProject}.
 */
public interface Project {

    /**
     * Get the plugin artifacts of the maven project.
     *
     * @return {@link java.util.Set} of {@link org.apache.maven.artifact.Artifact}'s
     */
    Set<Artifact> getPluginArtifacts();

    /**
     * Creates an artifact from dependency properties.
     *
     * @param groupId    {@link java.lang.String}
     * @param artifactId {@link java.lang.String}
     * @param version    {@link java.lang.String}
     * @param scope      {@link java.lang.String}
     * @return {@link org.apache.maven.artifact.Artifact}
     */
    Artifact createArtifact(final String groupId, final String artifactId, final String version, final String scope);

    /**
     * Get the artifact repository.
     *
     * @return {@link org.apache.maven.artifact.repository.ArtifactRepository}
     */
    ArtifactRepository getLocalRepository();

    /**
     * Get the artifact path in the maven repository.
     *
     * @param artifact {@link org.apache.maven.artifact.Artifact}
     * @return {@link java.lang.String}
     */
    String resolveArtifact(final Artifact artifact);

    /**
     * Get the plugin dependencies of the maven project.
     *
     * @return {@link java.util.List} of {@link org.apache.maven.model.Dependency}'s
     * @throws MafiaException thrown in case of an error
     */
    List<Dependency> getPluginDependencies() throws MafiaException;

    /**
     * Get the artifact dependencies of artifact.
     *
     * @param pluginArtifact {@link org.apache.maven.artifact.Artifact}
     * @return {@link java.util.List} of {@link org.apache.maven.model.Dependency}'s
     * @throws MafiaException thrown in case of an error
     */
    List<Dependency> getArtifactDependencies(final Artifact pluginArtifact) throws MafiaException;

    /**
     * Get the dependency path in the maven repository.
     *
     * @param dependency {@link org.apache.maven.model.Dependency}
     * @return {@link java.lang.String}
     */
    String resolveDependency(final Dependency dependency);

    /**
     * Get the artifacts from the maven project.
     *
     * @return @{link java.util.Set} of {@link org.apache.maven.artifact.Artifact}'s
     * @throws MafiaException thrown in case of an error
     */
    Set<Artifact> getArtifacts() throws MafiaException;
}
