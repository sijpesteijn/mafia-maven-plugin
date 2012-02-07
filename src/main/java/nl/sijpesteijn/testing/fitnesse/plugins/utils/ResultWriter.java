package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import nl.sijpesteijn.testing.fitnesse.plugins.executioners.TestSummaryAndDuration;

import org.apache.maven.plugin.MojoExecutionException;

public interface ResultWriter {

    void write(TestSummaryAndDuration summary) throws MojoExecutionException;

}
