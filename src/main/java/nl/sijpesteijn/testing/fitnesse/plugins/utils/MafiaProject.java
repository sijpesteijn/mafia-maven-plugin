package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.repository.RepositorySystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * MafiaProject.
 */
public class MafiaProject implements Project {

    /**
     * Separator char.
     */
    private static final char SEP = File.separatorChar;

    /**
     * Maven project.
     */
    private final MavenProject project;

    /**
     * Artifact repository.
     */
    private final ArtifactRepository localRepository;

    /**
     * Repository system.
     */
    private final RepositorySystem repositorySystem;

    /**
     * Maven project builder.
     */
    private final MavenProjectBuilder mavenProjectBuilder;

    /**
     * Remote repositories.
     */
    private final List<ArtifactRepository> remoteRepositories;

    /**
     * Graph builder.
     */
    private final GraphBuilder graphBuilder;

    /**
     * Constructor.
     *
     * @param project             - the mafia project.
     * @param localRepository     - the local repository.
     * @param repositorySystem    - the repository system.
     * @param mavenProjectBuilder - the maven project builder.
     * @param remoteRepositories  - the remote repositories.
     * @param graphBuilder        - the graph builder.
     */
    public MafiaProject(final MavenProject project,
                        final ArtifactRepository localRepository, final RepositorySystem repositorySystem,
                        final MavenProjectBuilder mavenProjectBuilder,
                        final List<ArtifactRepository> remoteRepositories, final GraphBuilder graphBuilder) {
        this.project = project;
        this.localRepository = localRepository;
        this.repositorySystem = repositorySystem;
        this.mavenProjectBuilder = mavenProjectBuilder;
        this.remoteRepositories = remoteRepositories;
        this.graphBuilder = graphBuilder;
    }

    /**
     * {@inheritDoc}
     */
    public final Set<Artifact> getPluginArtifacts() {
        return project.getPluginArtifacts();
    }

    /**
     * {@inheritDoc}
     */
    public final Artifact createArtifact(final String groupId, final String artifactId, final String version,
                                    final String scope) {
        return repositorySystem.createArtifact(groupId, artifactId, version, scope, "jar");
    }

    /**
     * {@inheritDoc}
     */
    public final ArtifactRepository getLocalRepository() {
        return localRepository;
    }

    /**
     * {@inheritDoc}
     */
    public final String resolveArtifact(final Artifact artifact) {
        return localRepository.getBasedir() + SEP + artifact.getGroupId().replace('.', SEP)
                + SEP + artifact.getArtifactId()
                + SEP + artifact.getVersion()
                + SEP + artifact.getArtifactId()
                + "-" + artifact.getVersion() + addClassifierIfPresent(artifact.getClassifier()) + ".jar";
    }

    /**
     * Check for classifier addition.
     *
     * @param classifier - classifier.
     * @return - string.
     */
    private String addClassifierIfPresent(final String classifier) {
        if (!StringUtils.isEmpty(classifier)) {
            return "-" + classifier;
        }
        return "";
    }

    /**
     * {@inheritDoc}
     */
    public final List<Dependency> getPluginDependencies() throws MafiaException {
        List<Dependency> dependencies = new ArrayList<Dependency>();
        Set<Artifact> pluginArtifacts = project.getPluginArtifacts();
        for (Artifact pluginArtifact : pluginArtifacts) {
            dependencies.addAll(getArtifactDependencies(pluginArtifact));
        }
        return dependencies;
    }

    /**
     * {@inheritDoc}
     */
    public final List<Dependency> getArtifactDependencies(final Artifact pluginArtifact) throws MafiaException {
        Artifact artifactPom = createArtifact(pluginArtifact.getGroupId(), pluginArtifact.getArtifactId(),
                pluginArtifact.getVersion(), pluginArtifact.getScope());
        try {
            MavenProject pluginProject =
                    mavenProjectBuilder.buildFromRepository(artifactPom, this.remoteRepositories, localRepository);
            return pluginProject.getDependencies();
        } catch (ProjectBuildingException e) {
            throw new MafiaException("Could not load plugin artifact pom.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public final String resolveDependency(final Dependency dependency) {
        return localRepository.getBasedir() + SEP + dependency.getGroupId().replace('.', SEP)
                + SEP + dependency.getArtifactId()
                + SEP + dependency.getVersion()
                + SEP + dependency.getArtifactId()
                + "-" + dependency.getVersion() + addClassifierIfPresent(dependency.getClassifier()) + ".jar";
    }

    /**
     * {@inheritDoc}
     */
    public final Set<Artifact> getArtifacts() throws MafiaException {
        return project.getArtifacts();
    }

}
