package nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs;

import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;

/**
 * Starter plugin configuration.
 * 
 */
public class StarterPluginConfig extends BasePluginConfig {

	private final List<String> jvmArguments;
	private final List<Dependency> jvmDependencies;
	private final List<Dependency> dependencies;

	public StarterPluginConfig(final String wikiRoot, final String nameRootPage, final String repositoryDirectory,
			final String logDirectory, final int fitnessePort, final int retainDays, final Log mavenLogger,
			final List<String> jvmArguments, final List<Dependency> jvmDependencies, final List<Dependency> dependencies) {
		super(wikiRoot, nameRootPage, repositoryDirectory, logDirectory, fitnessePort, retainDays, mavenLogger);
		this.jvmArguments = jvmArguments;
		this.jvmDependencies = jvmDependencies;
		this.dependencies = dependencies;
	}

	public List<String> getJvmArguments() {
		return jvmArguments;
	}

	public List<Dependency> getJvmDependencies() {
		return jvmDependencies;
	}

	public List<Dependency> getDependencies() {
		return dependencies;
	}

	/**
	 * Return the plugin configuration in one big string.
	 */
	// @Override
	// public String toString() {
	// return "Port: " + this.fitNessePort + ", Wiki Root: " + this.wikiRoot +
	// ", Name Root Page: "
	// + this.nameRootPage + ", Retain days: " + this.retainDays +
	// ", Log path: " + this.logPath
	// + ", JVM Arguments: " + this.jvmArguments.toString() +
	// ", JVM Dependencies: "
	// + this.jvmDependencies.toString() + ", Repository directory: " +
	// this.repositoryDirectory;
	// }

}
