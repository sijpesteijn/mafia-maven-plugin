package nl.sijpesteijn.testing.fitnesse.plugins.managers;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Interface for the plugin managers.
 * 
 */
public interface PluginManager {

	/**
	 * Do whatever the mojo should do.
	 * 
	 * @throws MojoFailureException
	 * @throws MojoExecutionException
	 */
	public void run() throws MojoFailureException, MojoExecutionException;
}