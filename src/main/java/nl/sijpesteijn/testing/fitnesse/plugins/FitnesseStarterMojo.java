package nl.sijpesteijn.testing.fitnesse.plugins;

import java.util.Arrays;
import java.util.List;

import nl.sijpesteijn.testing.fitnesse.plugins.managers.PluginManager;
import nl.sijpesteijn.testing.fitnesse.plugins.managers.PluginManagerFactory;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.PluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.StarterPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.StarterPluginConfig.Builder;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Goal to start a FitNesse instance
 * 
 * @goal start
 * 
 * @phase install
 */
public class FitnesseStarterMojo extends AbstractMojo {

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
    private String baseDir;

    /**
     * The port number FitNesse is running on.
     * 
     * @parameter expression="${start.port}" default-value="9090"
     */
    private String port;

    /**
     * The location for FitNesse to place the log files.
     * 
     * @parameter expression="${start.log}" default-value="${basedir}/log/"
     */
    private String log;

    /**
     * The number of days FitNesse retains test results.
     * 
     * @parameter expression="${start.retainDays}" default-value="14"
     */
    private String retainDays;

    /**
     * The location of the wiki root directory.
     * 
     * @parameter expression="${start.wikiRoot}" default-value="${basedir}"
     */
    private String wikiRoot;

    /**
     * The name of the wiki root page.
     * 
     * @parameter expression="${start.nameRootPage}" default-value="FitNesseRoot"
     */
    private String nameRootPage;

    /**
     * List of jvm arguments to pass to FitNesse
     * 
     * @parameter expression="${start.jvmArguments}"
     */
    private String[] jvmArguments;

    /**
     * List of dependency to add to the FitNesse start command.
     * 
     * @parameter expression="${start.jvmDependencies}"
     */
    private Dependency[] jvmDependencies;

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final PluginConfig starterPluginConfig = getPluginConfig();
        getLog().info("Starter config: " + starterPluginConfig.toString());
        final PluginManager pluginManager = PluginManagerFactory.getPluginManager(starterPluginConfig);
        pluginManager.run();
    }

    /**
     * Collect the plugin configuration settings
     * 
     * @return {@link nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.StarterPluginConfig}
     * @throws MojoExecutionException
     */
    private StarterPluginConfig getPluginConfig() throws MojoExecutionException {
        final Builder builder = PluginManagerFactory.getPluginConfigBuilder(StarterPluginConfig.class);
        builder.setPort(port);
        builder.setWikiRoot(wikiRoot);
        builder.setRetainDays(retainDays);
        builder.setNameRootPage(nameRootPage);
        builder.setLogLocation(log);
        builder.setJvmArguments(Arrays.asList(jvmArguments));
        builder.setJvmDependencies(Arrays.asList(jvmDependencies));
        builder.setDependencies(dependencies);
        builder.setBaseDir(baseDir);
        builder.setLogger(getLog());
        return builder.build();
    }

}
