package nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs;

import java.util.List;
import java.util.Map;

import nl.sijpesteijn.testing.fitnesse.plugins.utils.LogUtils;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.codehaus.plexus.component.annotations.Component;

/**
 * Content plugin configuration.
 * 
 */
@Component(role=PluginConfig.class, hint = "content-plugin-config")
public class ContentPluginConfig implements PluginConfig {

    private final String wikiRoot;
    private final String nameRootPage;
    private final List<String> statics;
    private final List<String> resources;
    private final List<Dependency> excludeDependencies;
    private final List<String> targets;
    private final List<String> compileClasspathElements;
    private final String repositoryDirectory;
    private final Map<String, Artifact> artifactMap;

    /**
     * 
     * @param wikiRoot
     *            {@link java.lang.String}
     * @param nameRootPage
     *            {@link java.lang.String}
     * @param statics
     *            {@link java.util.List}
     * @param resources
     *            {@link java.util.List}
     * @param excludeDependencies
     *            {@link java.util.List}
     * @param targets
     *            {@link java.util.List}
     * @param compileClasspathElements
     *            {@link java.util.List}
     * @param baseDir
     *            {@link java.lang.String}
     */
    public ContentPluginConfig(final String wikiRoot, final String nameRootPage, final List<String> statics,
            final List<String> resources, final List<Dependency> excludeDependencies, final List<String> targets,
            final List<String> compileClasspathElements, final String baseDir, final Map<String, Artifact> artifactMap) {
        this.wikiRoot = wikiRoot;
        this.nameRootPage = nameRootPage;
        this.statics = statics;
        this.resources = resources;
        this.excludeDependencies = excludeDependencies;
        this.targets = targets;
        this.compileClasspathElements = compileClasspathElements;
        this.repositoryDirectory = baseDir;
        this.artifactMap = artifactMap;
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

    public String getRepositoryDirectory() {
        return repositoryDirectory;
    }

    public Map<String, Artifact> getArtifactMap() {
        return artifactMap;
    }

    /**
     * 
     * The builder for this plugin configuration
     */
    public static class Builder implements PluginConfigBuilder {

        private String wikiRoot;
        private String nameRootPage;
        private List<String> statics;
        private List<String> resources;
        private List<Dependency> dependencies;
        private List<String> targets;
        private List<String> compileClasspathElements;
        private String repositoryDirectory;
        private Map<String, Artifact> artifactMap;

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

        public void setRepositoryDirectory(final String repositoryDirectory) {
            this.repositoryDirectory = repositoryDirectory;
        }

        public void setArtifactMap(final Map<String, Artifact> artifactMap) {
            this.artifactMap = artifactMap;
        }

        public ContentPluginConfig build() {
            return new ContentPluginConfig(wikiRoot, nameRootPage, statics, resources, dependencies, targets,
                    compileClasspathElements, repositoryDirectory, artifactMap);
        }
    }

    /**
     * Return the configuration in one big string.
     */
    @Override
    public String toString() {
        return "Repository directory: " + this.repositoryDirectory + ", Name root page: " + this.nameRootPage
                + ", Wiki root: " + this.wikiRoot + ", Statics: " + LogUtils.getString(this.statics) + ", Resources: "
                + LogUtils.getString(this.resources) + ", Targets: " + LogUtils.getString(this.targets)
                + ", Exclude dependencies: " + LogUtils.getStringFromDependencies(this.excludeDependencies)
                + ", Compile classpath elements: " + LogUtils.getString(this.compileClasspathElements);
    }

}
