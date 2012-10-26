package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Locate jars in the local repository.
 * 
 */
public class DependencyResolver {

	private final String repositoryDirectory;

	public DependencyResolver(final String repositoryDirectory) {
		this.repositoryDirectory = repositoryDirectory;
	}

	/**
	 * Find the part in the list of dependencies and return the location.
	 * 
	 * @param dependencies
	 *            {@link java.util.List}
	 * @param part
	 *            {@link java.lang.String}
	 * @param baseDir
	 *            {@link java.lang.String}
	 * @return {@link org.apache.maven.model.Dependency}
	 * @throws MojoExecutionException
	 */
	public String getJarLocation(final List<Dependency> dependencies,
			final Dependency partialDependency) throws MojoExecutionException {
		for (final Dependency dependency : dependencies) {
			if (isDependency(dependency, partialDependency)) {
				final String resolveDependencyPath = resolveDependencyPath(dependency);
				return FileUtils.formatPath(resolveDependencyPath);
			}
		}
		throw new MojoExecutionException(
				"Could not find jar location. Did you include dependency in your pom.xml? (groupId="
						+ partialDependency.getGroupId()
						+ ",artifactId="
						+ partialDependency.getArtifactId() + ")");
	}

	private boolean isDependency(final Dependency dependency,
			final Dependency partialDependency) {
		if (!dependency.getGroupId().equals(partialDependency.getGroupId())) {
			return false;
		}
		if (!dependency.getArtifactId().equals(
				partialDependency.getArtifactId())) {
			return false;
		}
		return true;
	}

	/**
	 * Return the location of the jar file.
	 * 
	 * @param dependency
	 * @param baseDir
	 * @return
	 */
	public String resolveDependencyPath(final Dependency dependency) {
		String resolved = repositoryDirectory.replace("\\", "/") + "/"
				+ dependency.getGroupId().replace(".", "/") + "/";
		resolved += dependency.getArtifactId() + "/" + dependency.getVersion()
				+ "/";
		resolved += dependency.getArtifactId() + "-" + dependency.getVersion();
		if (dependency.getClassifier() != null
				&& !dependency.getClassifier().equals("")) {
			resolved += "-" + dependency.getClassifier();
		}
		resolved += "." + dependency.getType();
		return resolved;
	}

}
