package nl.sijpesteijn.testing.fitnesse.plugins;

import nl.sijpesteijn.testing.fitnesse.plugins.managers.PluginManager;
import nl.sijpesteijn.testing.fitnesse.plugins.managers.PluginManagerFactory;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.PluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.StopperPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.StopperPluginConfig.Builder;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * Goal which creates the content.txt (classpath) file for Fitnesse.
 * 
 * @goal stop
 * 
 * @phase clean
 */
public class FitnesseStopperMojo extends AbstractMojo {

    /**
     * 
     * @parameter expression="${project}"
     * @required
     */
    protected MavenProject project;

    /**
     * Location of the local repository.
     * 
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    protected ArtifactRepository local;

    /**
     * @parameter expression="${stop.port}" default-value="9090"
     */
    protected String port;

    /**
     * 
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            final PluginConfig stopperPluginConfig = getPluginConfig();
            getLog().info("Stopper config: " + stopperPluginConfig.toString());
            final PluginManager pluginManager = PluginManagerFactory.getPluginManager(stopperPluginConfig);

            pluginManager.run();

        } catch (final Exception e) {
            throw new MojoExecutionException("Could not stop fitnesse.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private StopperPluginConfig getPluginConfig() throws MojoExecutionException {
        final Builder builder = PluginManagerFactory.getPluginConfigBuilder(StopperPluginConfig.class);
        builder.setPort(this.port);
        builder.setBaseDir(local.getBasedir());
        builder.setDependencies(project.getDependencies());
        return builder.build();
    }

}
