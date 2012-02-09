package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import nl.sijpesteijn.testing.fitnesse.plugins.executioners.TestSummaryAndDuration;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Result writer interface.
 * 
 */
public interface ResultWriter {

    /**
     * Save the summary information.
     * 
     * @param summary
     *        {@link nl.sijpesteijn.testing.fitnesse.plugins.executioners.TestSummaryAndDuration}
     * @throws MojoExecutionException
     */
    void write(TestSummaryAndDuration summary) throws MojoExecutionException;

}
