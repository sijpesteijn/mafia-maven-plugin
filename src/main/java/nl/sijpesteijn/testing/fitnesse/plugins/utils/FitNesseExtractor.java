package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import java.io.IOException;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

public class FitNesseExtractor {
	public static final String FITNESSE_VERSION = "20111025";

	public static void extract(final Log mavenLogger, final String fitnesseRoot, final String repositoryDirectory)
			throws MojoExecutionException {
		final DependencyResolver resolver = new DependencyResolver();
		final Dependency dependency = new Dependency();
		dependency.setArtifactId("fitnesse");
		dependency.setGroupId("org.fitnesse");
		dependency.setVersion(FITNESSE_VERSION);
		final String dependencyPath = resolver.resolveDependencyPath(dependency, repositoryDirectory);
		final CommandRunner runner = new CommandRunner(mavenLogger, fitnesseRoot);
		final String command = "java" + " -jar " + FileUtils.formatPath(dependencyPath) + " -i";
		try {
			runner.start(command, true, null);
		} catch (final IOException e) {
			throw new MojoExecutionException("Could not extract FitNesse", e);
		} catch (final InterruptedException e) {
			throw new MojoExecutionException("Could not extract FitNesse", e);
		}
	}

}
