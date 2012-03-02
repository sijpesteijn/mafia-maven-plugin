package nl.sijpesteijn.testing.fitnesse.plugins.managers;

import static org.junit.Assert.assertNotNull;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.PluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.StarterPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.StarterPluginConfig.Builder;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class PluginManagerFactoryTest {

	@Test
	public void getPluginManagers() throws Exception {
		final Builder builder = new StarterPluginConfig.Builder();
		final PluginManager starterPluginManager = PluginManagerFactory.getPluginManager(builder.build());
		assertNotNull(starterPluginManager);
	}

	@Test(expected = MojoExecutionException.class)
	public void getNotExisting() throws Exception {
		PluginManagerFactory.getPluginManager(new StubPluginConfig());
	}

	class StubPluginConfig implements PluginConfig {

	}
}
