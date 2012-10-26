package nl.sijpesteijn.testing.fitnesse.plugins;

import java.util.List;

import nl.sijpesteijn.testing.fitnesse.plugins.managers.StarterPluginManager;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.StarterPluginConfig;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Goal to start a FitNesse instance
 * 
 * @goal start
 * 
 */
public class FitNesseStarterMojo extends AbstractMojo {

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
	 * @parameter expression="${start.fitNessePort}" default-value="9090"
	 */
	private int fitNessePort;

	/**
	 * The location for FitNesse to place the log files.
	 * 
	 * @parameter expression="${start.logDirectory}"
	 *            default-value="${basedir}/log/"
	 */
	private String logDirectory;

	/**
	 * The number of days FitNesse retains test results.
	 * 
	 * @parameter expression="${start.retainDays}" default-value="14"
	 */
	private int retainDays;

	/**
	 * The location of the wiki root directory.
	 * 
	 * @parameter expression="${start.wikiRoot}" default-value="${basedir}"
	 */
	private String wikiRoot;

	/**
	 * The name of the wiki root page.
	 * 
	 * @parameter expression="${start.nameRootPage}"
	 *            default-value="FitNesseRoot"
	 */
	private String nameRootPage;

	/**
	 * List of jvm arguments to pass to FitNesse
	 * 
	 * @parameter expression="${start.jvmArguments}"
	 */
	private List<String> jvmArguments;

	/**
	 * List of dependency to add to the FitNesse start command.
	 * 
	 * @parameter expression="${start.jvmDependencies}"
	 */
	private List<Dependency> jvmDependencies;

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		final StarterPluginConfig starterPluginConfig = getPluginConfig();
		getLog().debug("Starter config: " + starterPluginConfig.toString());
		new StarterPluginManager(starterPluginConfig).run();
	}

	/**
	 * Collect the plugin configuration settings
	 * 
	 * @return {@link nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.StarterPluginConfig}
	 * @throws MojoExecutionException
	 */
	private StarterPluginConfig getPluginConfig() throws MojoExecutionException {
		return new StarterPluginConfig(wikiRoot, nameRootPage, repositoryDirectory, logDirectory, fitNessePort,
				retainDays, getLog(), jvmArguments, jvmDependencies, dependencies);
	}

}
