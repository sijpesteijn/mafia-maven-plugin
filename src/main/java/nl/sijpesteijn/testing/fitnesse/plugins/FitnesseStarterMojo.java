package nl.sijpesteijn.testing.fitnesse.plugins;

import java.util.Arrays;

import nl.sijpesteijn.testing.fitnesse.plugins.managers.PluginManager;
import nl.sijpesteijn.testing.fitnesse.plugins.managers.PluginManagerFactory;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.PluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.StarterPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.StarterPluginConfig.Builder;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Goal which creates the content.txt (classpath) file for Fitnesse.
 * 
 * @goal start
 * 
 * @phase install
 */
public class FitnesseStarterMojo extends AbstractMojo {
    /**
     * 
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    /**
     * Location of the local repository.
     * 
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    private ArtifactRepository local;

    /**
     * @parameter expression="${start.fitNessePort}" default-value="9090"
     */
    private String fitNessePort;

    /**
     * @parameter expression="${start.startSelenium}" default-value="false"
     */
    private boolean startSelenium;

    /**
     * @parameter expression="${start.seleniumPort}" default-value="9190"
     */
    private String seleniumPort;

    /**
     * @parameter expression="${start.log}" default-value="${basedir}/log/"
     */
    private String log;

    /**
     * @parameter expression="${start.retainDays}" default-value="14"
     */
    private String retainDays;

    /**
     * @parameter expression="${start.wikiRoot}" default-value="${basedir}"
     */
    private String wikiRoot;

    /**
     * @parameter expression="${start.nameRootPage}" default-value="FitNesseRoot"
     */
    private String nameRootPage;

    /**
     * 
     * @parameter expression="${start.jvmArguments}"
     */
    private String[] jvmArguments;

    /**
     * 
     * @parameter expression="${start.jvmDependencies}"
     */
    private Dependency[] jvmDependencies;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            final PluginConfig starterPluginConfig = getPluginConfig();
            getLog().info("Starter config: " + starterPluginConfig.toString());
            final PluginManager pluginManager = new PluginManagerFactory().getPluginManager(starterPluginConfig);

            pluginManager.run();
        } catch (final Exception e) {
            throw new MojoExecutionException("Could not start fitnesse.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private StarterPluginConfig getPluginConfig() {
        final Builder builder = new StarterPluginConfig.Builder();
        builder.setPort(fitNessePort);
        builder.setWikiRoot(wikiRoot);
        builder.setRetainDays(retainDays);
        builder.setNameRootPage(nameRootPage);
        builder.setLogLocation(log);
        builder.setJvmArguments(Arrays.asList(jvmArguments));
        builder.setJvmDependencies(Arrays.asList(jvmDependencies));
        builder.setDependencies(project.getDependencies());
        builder.setBaseDir(local.getBasedir());
        return builder.build();
    }

}
