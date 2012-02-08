package nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs;

import java.util.List;

import org.apache.maven.model.Dependency;

public class StarterPluginConfig implements PluginConfig {

    private final String fitNessePort;
    private final String wikiRoot;
    private final String retainDays;
    private final String nameRootPage;
    private final String logPath;
    private final List<String> jvmArguments;
    private final List<Dependency> jvmDependencies;
    private final List<Dependency> dependencies;
    private final String baseDir;

    public static class Builder implements PluginConfigBuilder {

        private String fitNessePort;
        private String wikiRoot;
        private String retainDays;
        private String nameRootPage;
        private String logPath;
        private List<String> jvmArguments;
        private List<Dependency> jvmDependencies;
        private List<Dependency> dependencies;
        private String baseDir;

        public void setPort(final String fitNessePort) {
            this.fitNessePort = fitNessePort;
        }

        public void setWikiRoot(final String wikiRoot) {
            this.wikiRoot = wikiRoot;
        }

        public void setRetainDays(final String retainDays) {
            this.retainDays = retainDays;
        }

        public void setNameRootPage(final String nameRootPage) {
            this.nameRootPage = nameRootPage;
        }

        public void setLogLocation(final String logPath) {
            this.logPath = logPath;
        }

        public void setJvmArguments(final List<String> jvmArguments) {
            this.jvmArguments = jvmArguments;
        }

        public void setJvmDependencies(final List<Dependency> jvmDependencies) {
            this.jvmDependencies = jvmDependencies;
        }

        public void setDependencies(final List<Dependency> dependencies) {
            this.dependencies = dependencies;
        }

        public void setBaseDir(final String baseDir) {
            this.baseDir = baseDir;
        }

        public StarterPluginConfig build() {
            return new StarterPluginConfig(fitNessePort, wikiRoot, retainDays, nameRootPage, logPath, jvmArguments,
                jvmDependencies, dependencies, baseDir);
        }
    }

    public StarterPluginConfig(final String fitNessePort, final String wikiRoot, final String retainDays,
                               final String nameRootPage, final String logPath, final List<String> jvmArguments,
                               final List<Dependency> jvmDependencies, final List<Dependency> dependencies,
                               final String baseDir)
    {
        this.fitNessePort = fitNessePort;
        this.wikiRoot = wikiRoot;
        this.retainDays = retainDays;
        this.nameRootPage = nameRootPage;
        this.logPath = logPath;
        this.jvmArguments = jvmArguments;
        this.jvmDependencies = jvmDependencies;
        this.dependencies = dependencies;
        this.baseDir = baseDir;
    }

    public String getFitNessePort() {
        return fitNessePort;
    }

    public String getWikiRoot() {
        return wikiRoot;
    }

    public String getRetainDays() {
        return retainDays;
    }

    public String getNameRootPage() {
        return nameRootPage;
    }

    public String getLogPath() {
        return logPath;
    }

    public List<String> getJvmArguments() {
        return jvmArguments;
    }

    public List<Dependency> getJvmDependencies() {
        return jvmDependencies;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public String getBaseDir() {
        return baseDir;
    }

    @Override
    public String toString() {
        return "FitNesse port: " + this.fitNessePort + ", Wiki Root: " + this.wikiRoot + ", Retain days: "
                + this.retainDays + ", Name Root Page: " + this.nameRootPage + ", Log path: " + this.logPath
                + ", JVM Arguments: " + this.jvmArguments.toString() + ", JVM Dependencies: "
                + this.jvmDependencies.toString() + ", base directory: " + this.baseDir;
    }

}
