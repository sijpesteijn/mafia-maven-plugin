package nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs;

import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;

/**
 * Stopper plugin configuration.
 * 
 */
public class StopperPluginConfig implements PluginConfig {

    private final int port;
    private final String repositoryDirectory;
    private final List<Dependency> dependencies;
    private final Log mavenLogger;

    public StopperPluginConfig(final int port, final String basedir, final List<Dependency> dependencies,
            final Log mavenLogger) {
        this.port = port;
        this.repositoryDirectory = basedir;
        this.dependencies = dependencies;
        this.mavenLogger = mavenLogger;
    }

    public int getPort() {
        return port;
    }

    public String getRepositoryDirectory() {
        return repositoryDirectory;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public Log getMavenLogger() {
        return mavenLogger;
    }

    /**
     * The builder for this configuration.
     * 
     */
    public static class Builder implements PluginConfigBuilder {

        private int port;
        private String repositoryDirectory;
        private List<Dependency> dependencies;
        private Log mavenLogger;

        public void setPort(final int port) {
            this.port = port;
        }

        public StopperPluginConfig build() {
            return new StopperPluginConfig(this.port, this.repositoryDirectory, this.dependencies, this.mavenLogger);
        }

        public void setRepositoryDirectory(final String basedir) {
            this.repositoryDirectory = basedir;
        }

        public void setDependencies(final List<Dependency> dependencies) {
            this.dependencies = dependencies;
        }

        public void setMavenLogger(final Log mavenLogger) {
            this.mavenLogger = mavenLogger;
        }
    }

    /**
     * Return the configuration in one big string.
     */
    @Override
    public String toString() {
        return "FitNesse port: " + this.port + ", Repository directory: " + this.repositoryDirectory;
    }

}
