package nl.sijpesteijn.testing.fitnesse.plugins;

import nl.sijpesteijn.testing.fitnesse.plugins.runner.FitNesseCommanderConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.FitNesseJarLocator;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaException;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaProject;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.Project;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.settings.Settings;

import java.io.File;
import java.util.List;

/**
 * AbstractFitNesseMojo.
 */
public abstract class AbstractFitNesseMojo extends AbstractMojo {

    /**
     * @link {org.apache.maven.settings.Settings}
     */
    @Component
    private Settings settings;

    /**
     * @link {org.apache.maven.project.MavenProject}
     */
    @Component
    private MavenProject project;

    /**
     * Used to look up Artifacts in the remote repository.
     */
    @Component
    private RepositorySystem repositorySystem;

    /**
     * Project builder -- builds a model from a pom.xml.
     */
    @Component
    private MavenProjectBuilder mavenProjectBuilder;

    /**
     * List of Remote Repositories used by the resolver.
     */
    @Parameter(defaultValue = "${project.remoteArtifactRepositories}", readonly = true, required = true)
    private List<ArtifactRepository> remoteRepositories;

    /**
     * Location of the local repository.
     */
    @Parameter(defaultValue = "${localRepository}", readonly = true, required = true)
    private ArtifactRepository localRepository;

    /**
     * The port number FitNesse is running on.
     */
    @Parameter(property = "fitNessePort", defaultValue = "9090")
    private int fitNessePort;

    /**
     * The location of the wiki root directory.
     */
    @Parameter(property = "wikiRoot", defaultValue = "${basedir}/")
    private String wikiRoot;

    /**
     * The name of the wiki root page.
     */
    @Parameter(property = "nameRootPage", defaultValue = "FitNesseRoot")
    private String nameRootPage;

    /**
     * The location for FitNesse to place the log files.
     */
    @Parameter(property = "logDirectory", defaultValue = "${basedir}/log/")
    private String logDirectory;

    /**
     * fitNesse user:password or file
     */
    @Parameter(property = "fitNesseAuthenticateStart", defaultValue = "")
    private String fitNesseAuthenticateStart;
    
    /**
     *  fitNesse user:password 
     */
    @Parameter(property = "fitNesseAuthenticateStop", defaultValue = "")
    private String fitNesseAuthenticateStop;


    /**
     * fitNesse UpdatePrevents
     */
    @Parameter(property = "fitNesseUpdatePrevents", defaultValue = "false")
    private Boolean fitNesseUpdatePrevents;
    
    /**
     * fitNesse Verbose
     */
    @Parameter(property = "fitNesseVerbose", defaultValue = "false")
    private Boolean fitNesseVerbose;



    /**
     * Get a commander configuration.
     *
     * @return {@link nl.sijpesteijn.testing.fitnesse.plugins.runner.FitNesseCommanderConfig}
     * @throws MojoFailureException thrown in cause of an error.
     */
    protected final FitNesseCommanderConfig getCommanderConfig() throws MojoFailureException {
        return getCommanderConfig(null, null, 0, fitNessePort, null);
    }

    protected final FitNesseCommanderConfig getCommanderConfig(String fitNesseAuthenticate) throws MojoFailureException {
        return getCommanderConfig(null, null, 0, fitNessePort, fitNesseAuthenticate);
    }

    /**
     * Get the mafia project.
     *
     * @return {@link nl.sijpesteijn.testing.fitnesse.plugins.utils.Project}
     * @throws MojoFailureException thrown in case of an error.
     */
    protected final Project getMafiaProject() throws MojoFailureException {
        return new MafiaProject(this.project, localRepository, repositorySystem,
                mavenProjectBuilder, remoteRepositories);
    }

    /**
     * Get the fitnesse commander configuration.
     *
     * @param jvmDependencies - the list of jvm dependencies.
     * @param jvmArguments    - the list of jvm arguments.
     * @param retainDays      - the number of days to preserve change history.
     * @param port            - the port to run fitnesse on.
     * @return {@link nl.sijpesteijn.testing.fitnesse.plugins.runner.FitNesseCommanderConfig}
     * @throws MojoFailureException thrown in case of an error.
     */
    protected final FitNesseCommanderConfig getCommanderConfig(final List<Dependency> jvmDependencies,
                                                               final List<String> jvmArguments,
                                                               final int retainDays, final int port, String fitNesseAuthenticateStart)
            throws MojoFailureException {
        try {
            final FitNesseJarLocator jarLocator = new FitNesseJarLocator(getMafiaProject());

            final String classpathString = createClasspathString(jvmDependencies, jarLocator.getFitNesseJarPath());

            return new FitNesseCommanderConfig(port, wikiRoot, nameRootPage, logDirectory, retainDays,
                    classpathString, jvmArguments, getLog(),
                    fitNesseAuthenticateStart, fitNesseAuthenticateStop, fitNesseUpdatePrevents, fitNesseVerbose);
        } catch (MafiaException e) {
            throw new MojoFailureException("Could not get command configuration.", e);
        }
    }

    /**
     * Create a classpath string out of the jvm dependencies and the fitnesse jar path.
     *
     * @param jvmDependencies - the jvm dependencies.
     * @param fitNesseJarPath - the fitnesse jar location in the local repository.
     * @return - the complete classpath for the command.
     */
    private String createClasspathString(final List<Dependency> jvmDependencies, final String fitNesseJarPath) {
        StringBuffer jvmStr = new StringBuffer();
        jvmStr.append(fitNesseJarPath);
        if (jvmDependencies == null) {
            return jvmStr.toString();
        }
        for (Dependency jvmDependency : jvmDependencies) {
            jvmStr.append(File.pathSeparatorChar);
            jvmStr.append(getDependencyPath(jvmDependency));
        }
        return jvmStr.toString();
    }

    /**
     * Get the path of the dependency in the local repository.
     *
     * @param dependency - the dependency
     * @return the path
     */
    private String getDependencyPath(final Dependency dependency) {
        final char sep = File.separatorChar;
        return localRepository.getBasedir() + sep + dependency.getGroupId().replace('.', sep)
                + sep + dependency.getArtifactId()
                + sep + dependency.getVersion()
                + sep + dependency.getArtifactId()
                + "-" + dependency.getVersion() + addClassifierIfPresent(dependency) + ".jar";
    }

    /**
     * Check the need for classifier addition.
     *
     * @param dependency - the dependency
     * @return the path
     */
    private String addClassifierIfPresent(final Dependency dependency) {
        if (!StringUtils.isEmpty(dependency.getClassifier())) {
            return "-" + dependency.getClassifier();
        }
        return "";
    }

    /**
     * Log an error.
     *
     * @param output - the output.
     * @param error  - the error.
     */
    protected final void logErrorMessages(final String output, final String error) {
        String[] errMsgs = error.split("\n");
        for (int i = 0; i < errMsgs.length; i++) {
            if (!errMsgs[i].equals("")) {
                getLog().error(errMsgs[i]);
            }
        }
        String[] outMsgs = output.split("\n");
        for (int i = 0; i < outMsgs.length; i++) {
            if (!outMsgs[i].equals("")) {
                getLog().error(outMsgs[i]);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "FitNesse port: " + fitNessePort
                + ", Wiki root: " + this.wikiRoot
                + ", Name of root page: " + this.nameRootPage
                + ", Log directory: " + logDirectory
                + ", fitNesseAuthenticateStart:" + fitNesseAuthenticateStart
                + ", fitNesseAuthenticateStop:" + fitNesseAuthenticateStop
                + ", fitNesseUpdatePrevents: " + fitNesseUpdatePrevents
                + ", fitNesseVerbose: " + fitNesseVerbose;
    }

    /**
     * @return {@link org.apache.maven.settings.Settings}
     */
    public final Settings getSettings() {
        return settings;
    }

    /**
     * @return {@link java.lang.String}
     */
    public final String getWikiRoot() {
        return wikiRoot;
    }

    /**
     * @return {@link java.lang.String}
     */
    public final String getNameRootPage() {
        return nameRootPage;
    }

    /**
     * @return int
     */
    public final int getFitNessePort() {
        return fitNessePort;
    }

    /**
     * @return {@link java.lang.String}
     */
    public String getFitNesseAuthenticateStart() {
        return fitNesseAuthenticateStart;
    }

    /**
     * @return {@link java.lang.String}
     */
    public String getFitNesseAuthenticateStop() {
        return fitNesseAuthenticateStop;
    }

    /**
     * @return
     */
    public Boolean getFitNesseUpdatePrevents() {
        return fitNesseUpdatePrevents;
    }

	public Boolean getFitNesseVerbose() {
		return fitNesseVerbose;
	}


}
