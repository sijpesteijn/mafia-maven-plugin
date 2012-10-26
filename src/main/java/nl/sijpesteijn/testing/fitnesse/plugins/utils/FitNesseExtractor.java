package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import java.io.IOException;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;

public class FitNesseExtractor {

	public static void extract(final String fitnesseRoot, final String repositoryDirectory,
			final List<Dependency> dependencies) throws MojoExecutionException {
		final DependencyResolver resolver = new DependencyResolver(repositoryDirectory);
		final Dependency dependency = new Dependency();
		dependency.setArtifactId("fitnesse");
		dependency.setGroupId("org.fitnesse");
		final String jarLocation = resolver.getJarLocation(dependencies, dependency);
		final CommandRunner runner = new CommandRunner(fitnesseRoot);
		final String command = "java" + " -jar " + FileUtils.formatPath(jarLocation) + " -i";
		try {
			runner.start(command);
			// Wait for 10 seconds for the jar command to finish
			Thread.sleep(10000);
		} catch (final IOException e) {
			throw new MojoExecutionException("Could not extract FitNesse", e);
		} catch (final InterruptedException e) {
			throw new MojoExecutionException("Could not extract FitNesse", e);
		}
	}

}
