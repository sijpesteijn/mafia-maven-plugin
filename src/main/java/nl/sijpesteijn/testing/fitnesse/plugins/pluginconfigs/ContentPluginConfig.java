package nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs;

import java.util.List;

import nl.sijpesteijn.testing.fitnesse.plugins.utils.LogUtils;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;

/**
 * Content plugin configuration.
 * 
 */
public class ContentPluginConfig extends BasePluginConfig {

	private final List<String> statics;
	private final List<String> resources;
	private final List<Dependency> excludeDependencies;
	private final List<String> targets;
	private final List<String> compileClasspathElements;

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
	 * @param repositoryDirectory
	 *            {@link java.lang.String}
	 */
	public ContentPluginConfig(final String wikiRoot, final String nameRootPage, final String repositoryDirectory,
			final String logDirectory, final int fitnessePort, final int retainDays, final Log mavenLogger,
			final List<String> statics, final List<String> resources, final List<Dependency> excludeDependencies,
			final List<String> targets, final List<String> compileClasspathElements) {
		super(wikiRoot, nameRootPage, repositoryDirectory, logDirectory, fitnessePort, retainDays, mavenLogger);
		this.statics = statics;
		this.resources = resources;
		this.excludeDependencies = excludeDependencies;
		this.targets = targets;
		this.compileClasspathElements = compileClasspathElements;
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

	/**
	 * Return the configuration in one big string.
	 */
	@Override
	public String toString() {
		return "Repository directory: " + getRepositoryDirectory() + ", Name root page: " + getNameRootPage()
				+ ", Wiki root: " + getWikiRoot() + ", Statics: " + LogUtils.getString(this.statics) + ", Resources: "
				+ LogUtils.getString(this.resources) + ", Targets: " + LogUtils.getString(this.targets)
				+ ", Exclude dependencies: " + LogUtils.getStringFromDependencies(this.excludeDependencies)
				+ ", Compile classpath elements: " + LogUtils.getString(this.compileClasspathElements);
	}

}
