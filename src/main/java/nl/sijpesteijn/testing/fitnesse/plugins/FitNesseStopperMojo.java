package nl.sijpesteijn.testing.fitnesse.plugins;

import nl.sijpesteijn.testing.fitnesse.plugins.runner.FitNesseCommander;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Goal to stop FitNesse instance.
 */
@Mojo(name = "stop", requiresDependencyResolution = ResolutionScope.TEST, threadSafe = true)
public class FitNesseStopperMojo extends AbstractFitNesseMojo {

    /**
     * {@inheritDoc}
     */
    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {
        getLog().debug(toString());
        final FitNesseCommander commander = new FitNesseCommander(getCommanderConfig());
        try {
            commander.stop();
        } catch (Throwable throwable) {
            throw new MojoExecutionException(throwable.getMessage());
        }
        if (commander.hasError()) {
            getLog().info("Could not stop FitNesse on port: " + getFitNessePort() + ". Are you sure it's running?");
            if (getLog().isDebugEnabled()) {
                logErrorMessages(commander.getOutput(), commander.getErrorOutput());
            }
        } else {
            getLog().info("FitNesse stopped on port: " + getFitNessePort());
        }
    }

}
