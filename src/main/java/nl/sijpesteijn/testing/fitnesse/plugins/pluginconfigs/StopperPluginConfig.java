package nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs;

import java.util.List;

import org.apache.maven.model.Dependency;

public class StopperPluginConfig implements PluginConfig {

    private final String port;
    private final String basedir;
    private final List<Dependency> dependencies;

    public StopperPluginConfig(final String port, final String basedir, final List<Dependency> dependencies) {
        this.port = port;
        this.basedir = basedir;
        this.dependencies = dependencies;
    }

    public String getPort() {
        return port;
    }

    public String getBasedir() {
        return basedir;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public static class Builder implements PluginConfigBuilder {

        private String port;
        private String basedir;
        private List<Dependency> dependencies;

        public void setPort(final String port) {
            this.port = port;
        }

        public StopperPluginConfig build() {
            return new StopperPluginConfig(this.port, this.basedir, this.dependencies);
        }

        public void setBaseDir(final String basedir) {
            this.basedir = basedir;
        }

        public void setDependencies(final List<Dependency> dependencies) {
            this.dependencies = dependencies;
        }

    }

}
