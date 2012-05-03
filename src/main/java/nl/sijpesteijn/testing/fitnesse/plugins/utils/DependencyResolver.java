package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Locate jars in the local repository.
 * 
 */
public class DependencyResolver {

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
	public String getJarLocation(final List<Dependency> dependencies, final Dependency partialDependency, final String baseDir)
			throws MojoExecutionException {
		for (Dependency dependency : dependencies) {
			if (isDependency(dependency, partialDependency)) {
				final String resolveDependencyPath = resolveDependencyPath(dependency, baseDir);
				return FileUtils.formatPath(resolveDependencyPath);
			}
		}
		throw new MojoExecutionException("Could not find jar location. Did you include dependency.(groupId=" + partialDependency.getGroupId() + ",artifactId=" + partialDependency.getArtifactId() +")");
	}

	private boolean isDependency(Dependency dependency,
			Dependency partialDependency) {
		if (!dependency.getGroupId().equals(partialDependency.getGroupId())) {
			return false;
		}
		if (!dependency.getArtifactId().equals(partialDependency.getArtifactId())) {
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
	public String resolveDependencyPath(final Dependency dependency, final String baseDir) {
		String resolved = baseDir.replace("\\", "/") + "/" + dependency.getGroupId().replace(".", "/") + "/";
		resolved += dependency.getArtifactId() + "/" + dependency.getVersion() + "/";
		resolved += dependency.getArtifactId() + "-" + dependency.getVersion();
		if (dependency.getClassifier() != null && !dependency.getClassifier().equals("")) {
			resolved += "-" + dependency.getClassifier();
		}
		resolved += "." + dependency.getType();
		return resolved;
	}

}
