package nl.sijpesteijn.testing.fitnesse.plugins;

import java.util.List;

import nl.sijpesteijn.testing.fitnesse.plugins.managers.StopperPluginManager;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.StopperPluginConfig;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Goal to stop FitNesse instance.
 * 
 * @goal stop
 * 
 */
public class FitNesseStopperMojo extends AbstractMojo {

	/**
	 * The Maven project instance for the executing project.
	 * <p>
	 * Note: This is passed by Maven and must not be configured by the user.
	 * </p>
	 * 
	 * @parameter expression="${project.dependencies}"
	 * @required
	 * @readonly
	 */
	private List<Dependency> dependencies;

	/**
	 * Location of the local repository.
	 * <p>
	 * Note: This is passed by Maven and must not be configured by the user.
	 * </p>
	 * 
	 * @parameter expression="${settings.localRepository}"
	 * @readonly
	 * @required
	 */
	private String repositoryDirectory;

	/**
	 * The port number FitNesse is running on.
	 * 
	 * @parameter expression="${stop.fitNessePort}" default-value="9090"
	 */
	private int fitNessePort;

	/**
	 * The location of the wiki root directory.
	 * 
	 * @parameter expression="${stop.wikiRoot}" default-value="${basedir}"
	 */
	private String wikiRoot;

	/**
	 * The name of the wiki root page.
	 * 
	 * @parameter expression="${stop.nameRootPage}" default-value="FitNesseRoot"
	 */
	private String nameRootPage;

	/**
	 * The location for FitNesse to place the log files.
	 * 
	 * @parameter expression="${stop.logDirectory}"
	 *            default-value="${basedir}/log/"
	 */
	private String logDirectory;

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		final StopperPluginConfig stopperPluginConfig = getPluginConfig();
		getLog().debug("Stopper config: " + stopperPluginConfig.toString());
		new StopperPluginManager(stopperPluginConfig).run();
	}

	/**
	 * Collect the plugin configuration settings
	 * 
	 * @return {@link nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.StopperPluginConfig}
	 * @throws MojoExecutionException
	 */
	private StopperPluginConfig getPluginConfig() throws MojoExecutionException {
		return new StopperPluginConfig(wikiRoot, nameRootPage, repositoryDirectory, logDirectory, fitNessePort, 0,
				getLog(), dependencies);
	}

}
