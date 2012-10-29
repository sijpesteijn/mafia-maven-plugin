package nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs;

import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;

/**
 * Stopper plugin configuration.
 * 
 */
public class StopperPluginConfig extends BasePluginConfig {

	public StopperPluginConfig(final String wikiRoot, final String nameRootPage, final String repositoryDirectory,
			final String logDirectory, final int fitnessePort, final int retainDays, final Log mavenLogger,
			final List<Dependency> dependencies) {
		super(wikiRoot, nameRootPage, repositoryDirectory, logDirectory, fitnessePort, retainDays, dependencies,
				mavenLogger);
	}

	/**
	 * Return the configuration in one big string.
	 */
	// @Override
	// public String toString() {
	// return "FitNesse port: " + this.port + ", Repository directory: " +
	// this.repositoryDirectory;
	// }

}
