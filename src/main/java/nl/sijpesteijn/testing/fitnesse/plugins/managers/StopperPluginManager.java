package nl.sijpesteijn.testing.fitnesse.plugins.managers;

import java.io.IOException;

import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.StopperPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.CommandRunner;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.DependencyResolver;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Plugin manager responsible for stopping FitNesse.
 * 
 */
public class StopperPluginManager implements PluginManager {

	private final StopperPluginConfig stopperPluginConfig;
	private final DependencyResolver resolver;

	/**
	 * 
	 * @param stopperPluginConfig
	 *            {@link nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.StopperPluginConfig}
	 * @throws MojoFailureException
	 */
	public StopperPluginManager(final StopperPluginConfig stopperPluginConfig) throws MojoFailureException {
		this.stopperPluginConfig = stopperPluginConfig;
		resolver = new DependencyResolver(stopperPluginConfig.getRepositoryDirectory());
	}

	/**
	 * Stop FitNesse
	 */
	@Override
	public void run() throws MojoFailureException, MojoExecutionException {
		final String jarLocation;
		try {
			final Dependency fitnesseDependency = new Dependency();
			fitnesseDependency.setGroupId("org.fitnesse");
			fitnesseDependency.setArtifactId("fitnesse");
			jarLocation = resolver.getJarLocation(stopperPluginConfig.getDependencies(), fitnesseDependency);
		} catch (final MojoExecutionException mee) {
			throw mee;
		}
		final String command = "java -cp " + jarLocation + " fitnesse.Shutdown -p "
				+ stopperPluginConfig.getFitnessePort();
		stopperPluginConfig.getMavenLogger().info(command);
		final CommandRunner runner = new CommandRunner(null);
		try {
			runner.start(command);
		} catch (final IOException e) {
			throw new MojoFailureException("Could not stop FitNesse", e);
		} catch (final InterruptedException e) {
			throw new MojoFailureException("Could not stop FitNesse", e);
		}
		if (runner.errorBufferHasContent()) {
			throw new MojoFailureException("Could not stop FitNesse. It might not be running?");
		}
		stopperPluginConfig.getMavenLogger().info("FitNesse stopped.");
	}

}
