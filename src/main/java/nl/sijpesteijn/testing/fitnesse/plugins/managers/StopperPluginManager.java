package nl.sijpesteijn.testing.fitnesse.plugins.managers;

import java.io.IOException;

import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.StopperPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.CommandRunner;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.DependencyResolver;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

public class StopperPluginManager implements PluginManager {

    private final DependencyResolver resolver;
    private final StopperPluginConfig stopperPluginConfig;

    public StopperPluginManager(final StopperPluginConfig stopperPluginConfig, final DependencyResolver resolver) {
        this.stopperPluginConfig = stopperPluginConfig;
        this.resolver = resolver;
    }

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
        final CommandRunner runner = new CommandRunner();
        try {
            runner.start(command);
        } catch (final IOException e) {
            throw new MojoFailureException("Could not start FitNesse", e);
        }
        if (runner.errorBufferHasContent()) {
            throw new MojoFailureException("Could not start FitNesse: " + runner.getErrorBufferMessage());
        }
    }

}
