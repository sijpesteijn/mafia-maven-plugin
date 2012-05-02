package nl.sijpesteijn.testing.fitnesse.plugins;

import java.util.List;

import nl.sijpesteijn.testing.fitnesse.plugins.managers.PluginManager;
import nl.sijpesteijn.testing.fitnesse.plugins.managers.PluginManagerFactory;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.PluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.StopperPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.StopperPluginConfig.Builder;

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
     * @parameter expression="${stop.port}" default-value="9090"
     */
    private int port;

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final PluginConfig stopperPluginConfig = getPluginConfig();
        getLog().debug("Stopper config: " + stopperPluginConfig.toString());
        final PluginManager pluginManager = PluginManagerFactory.getPluginManager(stopperPluginConfig);
        pluginManager.run();
    }

    /**
     * Collect the plugin configuration settings
     * 
     * @return {@link nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.StopperPluginConfig}
     * @throws MojoExecutionException
     */
    private StopperPluginConfig getPluginConfig() throws MojoExecutionException {
        final Builder builder = PluginManagerFactory.getPluginConfigBuilder(StopperPluginConfig.class);
        builder.setPort(this.port);
        builder.setRepositoryDirectory(repositoryDirectory);
        builder.setDependencies(dependencies);
        builder.setMavenLogger(getLog());
        return builder.build();
    }

}
