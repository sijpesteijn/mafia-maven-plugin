package nl.sijpesteijn.testing.fitnesse.plugins.runner;

import org.apache.maven.plugin.logging.Log;

import java.util.List;

/**
 * FitNesse commander configuration.
 */
public class FitNesseCommanderConfig {

    /**
     * Fitnesse port.
     */
    private final int fitNessePort;

    /**
     * Wiki root.
     */
    private final String wikiRoot;

    /**
     * jvm arguments.
     */
    private final List<String> jvmArguments;

    /**
     * Logger.
     */
    private final Log log;

    /**
     * FitNesse log directory.
     */
    private final String fitNesseLogDirectory;

    /**
     * Retain days.
     */
    private final int retainDays;

    /**
     * Class path string.
     */
    private String classpathString;

    /**
     * Name of root page.
     */
    private final String nameRootPage;

    /**
     * Time to wait after unpacking a new fitnesse.
     */
    private final long unpackWaitTime;
    
    
    /**
     * fitNesse authentication in start -a username:password
     *                         in stop  -c username password
     */
    private final String       fitNesseAuthenticate;
    
    
    /**
     * fitNesse Update Prevents -o
     */
    private final Boolean fitNesseUpdatePrevents;





	/**
     * Constructor.
     *
     * @param fitNessePort - fitnesse port.
     * @param wikiRoot - wiki root.
     * @param nameRootPage - name of root page.
     * @param fitNesseLogDirectory - fitnesse log directory.
     * @param retainDays - retain days.
     * @param classpathString - class path string.
     * @param jvmArguments - jvm arguments.
     * @param unpackWaitTime - time to wait after unpacking a new fitnesse.
     * @param log - logger.
     * @param fitNesseAuthenticate - fitNesse username password.
     * @param fitNesseUpdatePrevents - Update Prevents.
     * 
     */
    public FitNesseCommanderConfig(final int fitNessePort, final String wikiRoot, final String nameRootPage,
                                   final String fitNesseLogDirectory, final int retainDays,
                                   final String classpathString,
                                   final List<String> jvmArguments, final long unpackWaitTime, final Log log, 
                                   final String fitNesseAuthenticate,
                                   final Boolean fitNesseUpdatePrevents) {
        this.fitNessePort = fitNessePort;
        this.wikiRoot = wikiRoot;
        this.nameRootPage = nameRootPage;
        this.fitNesseLogDirectory = fitNesseLogDirectory;
        this.retainDays = retainDays;
        this.classpathString = classpathString;
        this.jvmArguments = jvmArguments;
        this.unpackWaitTime = unpackWaitTime;
        this.log = log;
        this.fitNesseAuthenticate = fitNesseAuthenticate;
        this.fitNesseUpdatePrevents = fitNesseUpdatePrevents;
        
    }

    /**
     * Get the wiki root.
     *
     * @return - wiki root.
     */
    public final String getWikiRoot() {
        return wikiRoot;
    }

    /**
     * Get the fitnesse port.
     *
     * @return fitnesse port.
     */
    public final int getFitNessePort() {
        return fitNessePort;
    }

    /**
     * Get the logger.
     *
     * @return - logger.
     */
    public final Log getLog() {
        return log;
    }

    /**
     * Get the jvm arguments.
     *
     * @return - jvm arguments.
     */
    public final List<String> getJvmArguments() {
        return jvmArguments;
    }

    /**
     * Get fitnesse log directory.
     *
     * @return - log directory.
     */
    public final String getFitNesseLogDirectory() {
        return fitNesseLogDirectory;
    }

    /**
     * Get the retain days.
     *
     * @return - retain days.
     */
    public final int getRetainDays() {
        return retainDays;
    }

    /**
     * Get the name of the root page.
     *
     * @return - name of root page.
     */
    public final String getNameRootPage() {
        return nameRootPage;
    }

    /**
     * Get the classpath string.
     *
     * @return - classpath.
     */
    public final String getClasspathString() {
        return classpathString;
    }

    public long getUnpackWaitTime() {
        return unpackWaitTime;
    }
    
    /**
     * Get the authenticate String
     * @return
     */
    public String getFitNesseAuthenticate() {
		return fitNesseAuthenticate;
	}


	public Boolean getFitNesseUpdatePrevents() {
		return fitNesseUpdatePrevents;
	}
}
