package nl.sijpesteijn.testing.fitnesse.plugins;

import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * AbstractStartFitNesseMojo.
 */
public abstract class AbstractStartFitNesseMojo extends AbstractFitNesseMojo {

    /**
     * The number of days of history.
     */
    @Parameter(property = "retainDays", defaultValue = "14")
    private int retainDays = 0;

    /**
     * List of jvm arguments to pass to FitNesse.
     */
    @Parameter(property = "jvmArguments")
    private List<String> jvmArguments;

    /**
     * List of dependencies to add to the FitNesse start command.
     */
    @Parameter(property = "jvmDependencies")
    private List<Dependency> jvmDependencies;
    
    /**
     * Mafia waits for the startup of FitNesse by trying to connect to it. This configuration determines how often Mafia
     * will attempt. Default: 4;
     */
    @Parameter(property = "connectionAttempts", defaultValue = "4")
    private int connectionAttempts;

    /**
     * The number of days to preserve the test history.
     *
     * @return int
     */
    public final int getRetainDays() {
        return retainDays;
    }

    /**
     * The jvm arguments for the fitnesse startup command.
     *
     * @return {@link java.util.List} of {@link java.lang.String}'s.
     */
    public final List<String> getJvmArguments() {
        return jvmArguments;
    }

    /**
     * The jvm dependencies for the fitnesse startup command.
     *
     * @return {@link java.util.List} of {@link org.apache.maven.model.Dependency}'s.
     */
    public final List<Dependency> getJvmDependencies() {
        return jvmDependencies;
    }
    
    public int getConnectionAttempts() {
        return connectionAttempts;
    }
}
