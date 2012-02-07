package nl.sijpesteijn.testing.fitnesse.plugins.managers;

import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.ContentPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.PluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.ReporterPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.RunnerPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.StarterPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.StopperPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.DependencyResolver;

import org.apache.maven.plugin.MojoExecutionException;

public class PluginManagerFactory {

    private final DependencyResolver resolver;

    public PluginManagerFactory() {
        resolver = new DependencyResolver();
    }

    public <PC extends PluginConfig> PluginManager getPluginManager(final PC pluginConfig)
            throws MojoExecutionException
    {
        if (pluginConfig instanceof RunnerPluginConfig) {
            return new RunnerPluginManager((RunnerPluginConfig) pluginConfig);
        }
        if (pluginConfig instanceof ReporterPluginConfig) {
            return new ReporterPluginManager((ReporterPluginConfig) pluginConfig);
        }
        if (pluginConfig instanceof StopperPluginConfig) {
            return new StopperPluginManager((StopperPluginConfig) pluginConfig, resolver);
        }
        if (pluginConfig instanceof StarterPluginConfig) {
            return new StarterPluginManager((StarterPluginConfig) pluginConfig, resolver);
        }
        if (pluginConfig instanceof ContentPluginConfig) {
            return new ContentPluginManager((ContentPluginConfig) pluginConfig, resolver);
        }
        throw new MojoExecutionException("No test manager found for : " + pluginConfig.toString());
    }

}
