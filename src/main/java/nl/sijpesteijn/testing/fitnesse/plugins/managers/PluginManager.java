package nl.sijpesteijn.testing.fitnesse.plugins.managers;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

public interface PluginManager {

    public void run() throws MojoFailureException, MojoExecutionException;
}
