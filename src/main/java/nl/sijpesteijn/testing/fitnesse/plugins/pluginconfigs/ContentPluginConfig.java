package nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs;

import java.util.List;

import org.apache.maven.model.Dependency;

public class ContentPluginConfig implements PluginConfig {

    private final String wikiRoot;
    private final String nameRootPage;
    private final List<String> statics;
    private final List<String> resources;
    private final List<Dependency> excludeDependencies;
    private final List<String> targets;
    private final List<String> compileClasspathElements;
    private final String baseDir;

    public ContentPluginConfig(final String wikiRoot, final String nameRootPage, final List<String> statics,
                               final List<String> resources, final List<Dependency> excludeDependencies,
                               final List<String> targets, final List<String> compileClasspathElements,
                               final String baseDir)
    {
        this.wikiRoot = wikiRoot;
        this.nameRootPage = nameRootPage;
        this.statics = statics;
        this.resources = resources;
        this.excludeDependencies = excludeDependencies;
        this.targets = targets;
        this.compileClasspathElements = compileClasspathElements;
        this.baseDir = baseDir;
    }

    public String getWikiRoot() {
        return wikiRoot;
    }

    public String getNameRootPage() {
        return nameRootPage;
    }

    public List<String> getStatics() {
        return statics;
    }

    public List<String> getResources() {
        return resources;
    }

    public List<Dependency> getExcludeDependencies() {
        return excludeDependencies;
    }

    public List<String> getTargets() {
        return targets;
    }

    public List<String> getCompileClasspathElements() {
        return compileClasspathElements;
    }

    public String getBasedir() {
        return baseDir;
    }

    public static class Builder {

        private String wikiRoot;
        private String nameRootPage;
        private List<String> statics;
        private List<String> resources;
        private List<Dependency> dependencies;
        private List<String> targets;
        private List<String> compileClasspathElements;
        private String baseDir;

        public void setWikiRoot(final String wikiRoot) {
            this.wikiRoot = wikiRoot;
        }

        public void setNameRootPage(final String nameRootPage) {
            this.nameRootPage = nameRootPage;
        }

        public void setStatics(final List<String> statics) {
            this.statics = statics;
        }

        public void setResources(final List<String> resources) {
            this.resources = resources;
        }

        public void setExcludeDependencies(final List<Dependency> excludeDependencies) {
            this.dependencies = excludeDependencies;
        }

        public void setTargets(final List<String> targets) {
            this.targets = targets;
        }

        public void setCompileClasspathElements(final List<String> compileClasspathElements) {
            this.compileClasspathElements = compileClasspathElements;
        }

        public void setBaseDir(final String baseDir) {
            this.baseDir = baseDir;
        }

        public ContentPluginConfig build() {
            return new ContentPluginConfig(wikiRoot, nameRootPage, statics, resources, dependencies, targets,
                compileClasspathElements, baseDir);
        }
    }

}
