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

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Factory method approach for getting plugin configs & plugin managers.
 * 
 */
public class PluginManagerFactory {

	/**
	 * List with all the possible plugin configurations.
	 */
	private static Map<Class<? extends PluginConfig>, PluginConfigBuilder> pluginConfigs = new HashMap<Class<? extends PluginConfig>, PluginConfigBuilder>();
	static {
		pluginConfigs.put(ContentPluginConfig.class, new ContentPluginConfig.Builder());
		pluginConfigs.put(ReporterPluginConfig.class, new ReporterPluginConfig.Builder());
		pluginConfigs.put(RunnerPluginConfig.class, new RunnerPluginConfig.Builder());
		pluginConfigs.put(StarterPluginConfig.class, new StarterPluginConfig.Builder());
		pluginConfigs.put(StopperPluginConfig.class, new StopperPluginConfig.Builder());
	}

	/**
	 * 
	 * @param pluginConfig
	 *            {@link nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.PluginConfig}
	 * @return {@link nl.sijpesteijn.testing.fitnesse.plugins.managers.PluginManager}
	 * @throws MojoExecutionException
	 */
	public static <PC extends PluginConfig> PluginManager getPluginManager(final PC pluginConfig)
			throws MojoExecutionException {
		if (pluginConfig instanceof RunnerPluginConfig) {
			return new RunnerPluginManager((RunnerPluginConfig) pluginConfig);
		}
		if (pluginConfig instanceof ReporterPluginConfig) {
			return new ReporterPluginManager((ReporterPluginConfig) pluginConfig);
		}
		if (pluginConfig instanceof StopperPluginConfig) {
			return new StopperPluginManager((StopperPluginConfig) pluginConfig);
		}
		if (pluginConfig instanceof StarterPluginConfig) {
			return new StarterPluginManager((StarterPluginConfig) pluginConfig);
		}
		if (pluginConfig instanceof ContentPluginConfig) {
			return new ContentPluginManager((ContentPluginConfig) pluginConfig);
		}
		throw new MojoExecutionException("No plugin manager found for : " + pluginConfig.toString());
	}

	/**
	 * 
	 * @param clazz
	 *            {@link java.lang.Class}
	 * @return {@link nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.PluginConfigBuilder}
	 * @throws MojoExecutionException
	 */
	@SuppressWarnings("unchecked")
	public static <B> B getPluginConfigBuilder(final Class<? extends PluginConfig> clazz) throws MojoExecutionException {
		final PluginConfigBuilder builder = pluginConfigs.get(clazz);
		if (builder == null) {
			throw new MojoExecutionException("No plugin config builder found for: " + clazz.getName());
		}
		return (B) builder;
	}

}
