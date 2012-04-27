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
	 * @return {@link java.lang.String}
	 * @throws MojoExecutionException
	 */
	public String getJarLocation(final List<Dependency> dependencies, final String part, final String baseDir)
			throws MojoExecutionException {
		for (int i = 0; i < dependencies.size(); i++) {
			final Dependency dependency = dependencies.get(i);
			final String resolveDependencyPath = resolveDependencyPath(dependency, baseDir);
			if (resolveDependencyPath.contains(part)) {
				return FileUtils.formatPath(resolveDependencyPath);
			}
		}
		throw new MojoExecutionException("Could not find jar location.");
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
