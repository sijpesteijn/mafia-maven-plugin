package nl.sijpesteijn.testing.fitnesse.plugins;

import java.util.Arrays;

import nl.sijpesteijn.testing.fitnesse.plugins.managers.PluginManager;
import nl.sijpesteijn.testing.fitnesse.plugins.managers.PluginManagerFactory;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.ContentPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.ContentPluginConfig.Builder;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.PluginConfig;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * Goal which creates the content.txt (classpath) file for Fitnesse.
 * 
 * @goal content
 * 
 * @phase process-sources
 * @requiresDependencyResolution compile+runtime
 */
public class FitnesseContentMojo extends AbstractMojo {

	/**
	 * The Maven project instance for the executing project.
	 * <p>
	 * Note: This is passed by Maven and must not be configured by the user.
	 * </p>
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * Location of the local repository.
	 * <p>
	 * Note: This is passed by Maven and must not be configured by the user.
	 * </p>
	 * 
	 * @parameter expression="${localRepository}"
	 * @readonly
	 * @required
	 */
	private ArtifactRepository local;

	/**
	 * List of static entries to add to content.txt
	 * 
	 * @parameter expression="${content.statics}"
	 */
	private String[] statics;

	/**
	 * List of target directories to add to content.txt Each target is resolved
	 * as <target>\target\classes
	 * 
	 * @parameter expression="${content.targets}"
	 */
	private String[] targets;

	/**
	 * List of resource entries to add to content.txt
	 * 
	 * @parameter expression="${content.resources}"
	 */
	private String[] resources;

	/**
	 * List of dependencies to exclude from the content.txt
	 * 
	 * @parameter expression="${content.excludeDependencies}"
	 */
	private Dependency[] excludeDependencies;

	/**
	 * Location of the wiki root of FitNesse.
	 * 
	 * @parameter expression="${content.wikiRoot}" default-value="${basedir}"
	 * @required
	 */
	private String wikiRoot;

	/**
	 * Name of the wiki root page
	 * 
	 * @parameter expression="${content.nameRootPage}"
	 *            default-value="FitNesseRoot"
	 * @required
	 */
	private String nameRootPage;

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		final PluginConfig contentPluginConfig = getPluginConfig();
		getLog().info("Content config: " + contentPluginConfig.toString());
		final PluginManager pluginManager = PluginManagerFactory.getPluginManager(contentPluginConfig);
		pluginManager.run();
	}

	/**
	 * Collect the plugin configuration settings
	 * 
	 * @return {@link nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.ContentPluginConfig}
	 * @throws DependencyResolutionRequiredException
	 */
	private ContentPluginConfig getPluginConfig() throws MojoExecutionException {
		final Builder builder = PluginManagerFactory.getPluginConfigBuilder(ContentPluginConfig.class);
		builder.setWikiRoot(wikiRoot);
		builder.setNameRootPage(nameRootPage);
		builder.setStatics(Arrays.asList(statics));
		builder.setResources(Arrays.asList(resources));
		builder.setExcludeDependencies(Arrays.asList(excludeDependencies));
		builder.setTargets(Arrays.asList(targets));
		try {
			builder.setCompileClasspathElements(project.getCompileClasspathElements());
		} catch (final DependencyResolutionRequiredException e) {
			throw new MojoExecutionException("Could not get compile classpath elements: ", e);
		}
		builder.setBaseDir(local.getBasedir());
		return builder.build();
	}

}
