package nl.sijpesteijn.testing.fitnesse.plugins.managers;

import java.util.HashMap;
import java.util.Map;

import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.ContentPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.PluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.PluginConfigBuilder;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.ReporterPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.RunnerPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.StarterPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.StopperPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.DependencyResolver;

import org.apache.maven.plugin.MojoExecutionException;

public class PluginManagerFactory {

    private static final DependencyResolver resolver;
    private static Map<Class<? extends PluginConfig>, PluginConfigBuilder> pluginConfigs =
            new HashMap<Class<? extends PluginConfig>, PluginConfigBuilder>();

    static {
        resolver = new DependencyResolver();
        pluginConfigs.put(ContentPluginConfig.class, new ContentPluginConfig.Builder());
        pluginConfigs.put(ReporterPluginConfig.class, new ReporterPluginConfig.Builder());
        pluginConfigs.put(RunnerPluginConfig.class, new RunnerPluginConfig.Builder());
        pluginConfigs.put(StarterPluginConfig.class, new StarterPluginConfig.Builder());
        pluginConfigs.put(StopperPluginConfig.class, new StopperPluginConfig.Builder());
    }

    public static <PC extends PluginConfig> PluginManager getPluginManager(final PC pluginConfig)
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
        throw new MojoExecutionException("No plugin manager found for : " + pluginConfig.toString());
    }

    @SuppressWarnings("unchecked")
    public static <B> B getPluginConfigBuilder(final Class<? extends PluginConfig> clazz) throws MojoExecutionException
    {
        final PluginConfigBuilder builder = pluginConfigs.get(clazz);
        if (builder == null) {
            throw new MojoExecutionException("No plugin config builder found for: " + clazz.getName());
        }
        return (B) builder;
    }

}
