package nl.sijpesteijn.testing.fitnesse.plugins.managers;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.codehaus.plexus.util.FileUtils;

public class ReportResource {
    private String resourcePathBase;

    private File outputDirectory;

    public ReportResource(final String resourcePathBase, final File outputDirectory) {
        this.resourcePathBase = resourcePathBase;
        this.outputDirectory = outputDirectory;
    }

    public void copy(final String resourceName) throws IOException {
        final File resource = new File(outputDirectory, resourceName);
        if ((resource != null) && (!resource.exists())) {
            final URL url = Thread.currentThread().getContextClassLoader()
                    .getResource(resourcePathBase + File.separatorChar + resourceName);
            if (url != null) {
                FileUtils.copyURLToFile(url, resource);
            }
        }
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public String getResourcePathBase() {
        return resourcePathBase;
    }

    public void setResourcePathBase(final String resourcePathBase) {
        this.resourcePathBase = resourcePathBase;
    }

}
