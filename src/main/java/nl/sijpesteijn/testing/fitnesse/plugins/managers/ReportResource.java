package nl.sijpesteijn.testing.fitnesse.plugins.managers;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.codehaus.plexus.util.FileUtils;

public class ReportResource {
    private final String resourcePathBase;

    private final String outputDirectory;

    public ReportResource(final String resourcePathBase, final String outputDirectory) {
        this.resourcePathBase = resourcePathBase;
        this.outputDirectory = outputDirectory;
    }

    public void copy(final String resourceName) throws IOException {
        final File destination = new File(outputDirectory, resourceName);
        if ((destination != null) && !destination.exists()) {
            final URL url =
                    Thread.currentThread().getContextClassLoader().getResource(resourcePathBase + "/" + resourceName);
            if (url != null) {
                FileUtils.copyURLToFile(url, destination);
            }
        }
    }
}
