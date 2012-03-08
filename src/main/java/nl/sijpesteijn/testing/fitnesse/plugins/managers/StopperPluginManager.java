package nl.sijpesteijn.testing.fitnesse.plugins.managers;

import java.io.IOException;

import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.StopperPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.CommandRunner;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.DependencyResolver;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Plugin manager responsible for stopping FitNesse.
 * 
 */
public class StopperPluginManager implements PluginManager {

    private final StopperPluginConfig stopperPluginConfig;
    private final DependencyResolver resolver = new DependencyResolver();

    /**
     * 
     * @param stopperPluginConfig
     *        {@link nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.StopperPluginConfig}
     */
    public StopperPluginManager(final StopperPluginConfig stopperPluginConfig) {
        this.stopperPluginConfig = stopperPluginConfig;
    }

    /**
     * Stop FitNesse
     */
    @Override
    public void run() throws MojoFailureException, MojoExecutionException {
        final String jarLocation;
        try {
            jarLocation =
                    resolver.getJarLocation(stopperPluginConfig.getDependencies(), "org/fitnesse/",
                        stopperPluginConfig.getBasedir());
        } catch (final MojoExecutionException mee) {
            throw mee;
        }
        final String command = "java -cp " + jarLocation + " fitnesse.Shutdown -p " + stopperPluginConfig.getPort();
        stopperPluginConfig.getLog().info(command);
        final CommandRunner runner = new CommandRunner(stopperPluginConfig.getLog());
        try {
            runner.start(command, true, null);
        } catch (final IOException e) {
            throw new MojoFailureException("Could not stop FitNesse", e);
        } catch (final InterruptedException e) {
            throw new MojoFailureException("Could not stop FitNesse", e);
        }
        if (runner.errorBufferHasContent()) {
            throw new MojoFailureException("Could not stop FitNesse: " + runner.getErrorBufferMessage());
        }
    }

}
