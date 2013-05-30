package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.model.Dependency;

import java.util.List;
import java.util.Set;

/**
 * FitNesseJarLocator.
 */
public class FitNesseJarLocator {

    /**
     * Project.
     */
    private final Project project;

    /**
     * FitNesse artifact.
     */
    private Artifact fitNesseArtifact;

    /**
     * Constructor.
     *
     * @param project - the mafia project.
     * @throws MafiaException thrown in case of an error.
     */
    public FitNesseJarLocator(final Project project)
            throws MafiaException {
        this.project = project;
    }

    /**
     * Find fitnesse jar.
     *
     * @throws MafiaException - unable to find fitnesse jar.
     */
    private void findFitNesseJar() throws MafiaException {
        findInProjectDependencies();
        if (fitNesseArtifact == null) {
            findInPluginDependencies();
        }
    }

    /**
     * Find in dependencies.
     *
     * @throws MafiaException - unable to find fitnesse jar in dependencies.
     */
    private void findInPluginDependencies() throws MafiaException {
        Artifact mafiaPlugin = findMafiaPlugin(project.getPluginArtifacts());
        if (mafiaPlugin != null) {
            Artifact artifactPom = project.createArtifact(mafiaPlugin.getGroupId(), mafiaPlugin.getArtifactId(),
                    mafiaPlugin.getVersion(), mafiaPlugin.getScope());
            List<Dependency> artifactDependencies = project.getArtifactDependencies(artifactPom);
            findFitNesseDependency(artifactDependencies);
        }
    }

    /**
     * Find fitnesse in dependencies.
     *
     * @param dependencies - list of dependencies.
     */
    private void findFitNesseDependency(final List<Dependency> dependencies) {
        for (Dependency dependency : dependencies) {
            Artifact artifact = new DefaultArtifact(dependency.getGroupId(), dependency.getArtifactId(),
                    dependency.getVersion(), dependency.getScope(), dependency.getType(),
                    dependency.getClassifier(), new DefaultArtifactHandler());
            if (isFitNesseJar(artifact)) {
                updateFitNesseJar(artifact);
            }
        }
    }

    /**
     * Find the mafia plugin.
     *
     * @param artifacts - list of artifacts.
     * @return - mafia artifact.
     */
    private Artifact findMafiaPlugin(final Set<Artifact> artifacts) {
        if (artifacts != null) {
            for (Artifact artifact : artifacts) {
                if (artifact.getArtifactId().equals("mafia-maven-plugin")) {
                    return artifact;
                }
            }
        }
        return null;
    }

    /**
     * Find fitnesse jar in project dependencies.
     *
     * @throws MafiaException - unable to find fitnesse jar in project dependencies.
     */
    private void findInProjectDependencies() throws MafiaException {
        Set<Artifact> compileArtifacts = project.getArtifacts(); //DefaultArtifact.SCOPE_COMPILE);
        searchArtifacts(compileArtifacts);
    }

    /**
     * Find fitnesse jar in list of artifacts.
     *
     * @param artifacts - list of artifacts.
     */
    private void searchArtifacts(final Set<Artifact> artifacts) {
        if (artifacts != null) {
            for (Artifact artifact : artifacts) {
                if (isFitNesseJar(artifact)) {
                    updateFitNesseJar(artifact);
                }
            }
        }
    }

    /**
     * Update the found fitnesse jar.
     *
     * @param artifact - fitnesse artifact.
     */
    private void updateFitNesseJar(final Artifact artifact) {
        if (fitNesseArtifact == null) {
            fitNesseArtifact = artifact;
        }
    }

    /**
     * Check if the artifact is a fitnesse artifact.
     *
     * @param artifact - the artifact.
     * @return - boolean.
     */
    private boolean isFitNesseJar(final Artifact artifact) {
        return artifact.getGroupId().equals("org.fitnesse");
    }

    /**
     * Return the fitnesse jar path in the local repository.
     *
     * @return - the path to the fitnesse jar.
     * @throws MafiaException thrown in case of an error.
     */
    public final String getFitNesseJarPath() throws MafiaException {
        findFitNesseJar();
        if (fitNesseArtifact == null) {
            throw new MafiaException("Could not find any fitnesse dependency in the pom information."
                    + " Did you add a FitNesse depedency?");
        }
        return project.resolveArtifact(fitNesseArtifact);
    }
}
