package nl.sijpesteijn.testing.fitnesse.plugins;

import nl.sijpesteijn.testing.fitnesse.plugins.runner.FitNesseCommander;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Goal to start a FitNesse instance.
 */
@Mojo(name = "start")
public class FitNesseStarterMojo extends AbstractStartFitNesseMojo {

    /**
     * {@inheritDoc}
     */
    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {
        getLog().debug(toString());
        final FitNesseCommander commander = new FitNesseCommander(getCommanderConfig(getJvmDependencies(),
                getJvmArguments(),
                getRetainDays(), getFitNessePort()));
        try {
            commander.start();
        } catch (MafiaException me) {
            throw new MojoExecutionException(me.getMessage(), me);
        }
        if (commander.hasError()) {
            logErrorMessages(commander.getOutput(), commander.getErrorOutput());
            throw new MojoExecutionException("Could not start FitNesse");
        } else {
            getLog().info("FitNesse start on: http://localhost:" + getFitNessePort());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return super.toString()
                + ", Retain days: " + getRetainDays() + ", JVM arguments: " + getJvmArguments()
                + ", JVM dependencies: " + getJvmDependencies();
    }

}
