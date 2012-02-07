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
     * 
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    /**
     * 
     * @parameter expression="${content.statics}"
     */
    private String[] statics;

    /**
     * 
     * @parameter expression="${content.targets}"
     */
    private String[] targets;

    /**
     * 
     * @parameter expression="${content.resources}"
     */
    private String[] resources;

    /**
     * 
     * @parameter expression="${content.excludeDependencies}"
     */
    private Dependency[] excludeDependencies;

    /**
     * Location of the local repository.
     * 
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    private ArtifactRepository local;

    /**
     * Location of the file.
     * 
     * @parameter expression="${content.wikiRoot}" default-value="${basedir}"
     * @required
     */
    private String wikiRoot;

    /**
     * Naam van de wiki root pagina
     * 
     * @parameter expression="${content.nameRootPage}" default-value="FitNesseRoot"
     * @required
     */
    private String nameRootPage;

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            final PluginConfig contentPluginConfig = getPluginConfig();
            getLog().info("Content config: " + contentPluginConfig.toString());
            final PluginManager pluginManager = new PluginManagerFactory().getPluginManager(contentPluginConfig);
            pluginManager.run();

        } catch (final Exception e) {
            throw new MojoExecutionException("Could not create fitnesse content.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private ContentPluginConfig getPluginConfig() throws DependencyResolutionRequiredException {
        final Builder builder = new ContentPluginConfig.Builder();
        builder.setWikiRoot(wikiRoot);
        builder.setNameRootPage(nameRootPage);
        builder.setStatics(Arrays.asList(statics));
        builder.setResources(Arrays.asList(resources));
        builder.setExcludeDependencies(Arrays.asList(excludeDependencies));
        builder.setTargets(Arrays.asList(targets));
        builder.setCompileClasspathElements(project.getCompileClasspathElements());
        builder.setBaseDir(local.getBasedir());
        return builder.build();
    }

}
