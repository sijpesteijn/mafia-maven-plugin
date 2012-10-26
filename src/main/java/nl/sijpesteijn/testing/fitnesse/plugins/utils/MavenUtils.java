package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public class MavenUtils {

	private final MavenProject project;

	public MavenUtils(final MavenProject project) {
		this.project = project;
	}

	public Xpp3Dom getPluginConfiguration(final String groupId, final String artifactId, final String goal) {
		final Plugin plugin = findPlugin(project, groupId, artifactId, goal);
		if (plugin == null) {
			return null;
		}
		return (Xpp3Dom) plugin.getConfiguration();
	}

	private Plugin findPlugin(final MavenProject project, final String groupId, final String artifactId,
			final String goal) {
		final List<Profile> activeProfiles = project.getModel().getProfiles();
		for (final Profile profile : activeProfiles) {
			final Plugin plugin = getMafiaPlugin(groupId, artifactId, profile.getBuild(), goal);
			if (plugin != null) {
				return plugin;
			}
		}
		return getMafiaPlugin(groupId, artifactId, project.getBuild(), goal);
	}

	private Plugin getMafiaPlugin(final String groupId, final String artifactId, final BuildBase build,
			final String goal) {
		final List<Plugin> plugins = build.getPlugins();
		for (final Plugin plugin : plugins) {
			if (isPlugin(plugin, groupId, artifactId) && hasGoal(plugin, goal)) {
				return plugin;
			}
		}
		return null;
	}

	private boolean hasGoal(final Plugin plugin, final String goalToFind) {
		final List<PluginExecution> executions = plugin.getExecutions();
		for (final PluginExecution pluginExecution : executions) {
			final List<String> goals = pluginExecution.getGoals();
			for (final String goal : goals) {
				if (goal.equals(goalToFind)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isPlugin(final Plugin plugin, final String groupId, final String artifactId) {
		if (plugin.getGroupId().equals(groupId) && plugin.getArtifactId().equals(artifactId)) {
			return true;
		}
		return false;
	}

	public String getStringValueFromConfiguration(final Xpp3Dom configuration, final String name,
			final String defaultValue) {
		if (configuration != null) {
			final Xpp3Dom child = configuration.getChild(name);
			if (child != null) {
				return resolvePlaceHolder(child.getValue());
			}
		}
		return defaultValue != null ? resolvePlaceHolder(defaultValue) : null;
	}

	public List<String> getStringArrayFromConfiguration(final Xpp3Dom configuration, final String name) {
		final List<String> statics = new ArrayList<String>();
		if (configuration != null) {
			final Xpp3Dom child = configuration.getChild(name);
			if (child != null) {
				for (int i = 0; i < child.getChildCount(); i++) {
					statics.add(resolvePlaceHolder(child.getChild(i).getValue()));
				}
			}
		}
		return statics;
	}

	private String resolvePlaceHolder(final String value) {
		final int start = value.indexOf("${");
		final int stop = value.indexOf("}");
		if (start > -1 && stop > -1) {
			final String propertyName = getPropertyValue(value.substring(start + 2, stop));
			if (propertyName != null) {
				return value.substring(0, start) + propertyName + value.substring(stop + 1, value.length());
			}
		}
		return value;
	}

	private String getPropertyValue(final String propertyName) {
		final Properties properties = project.getModel().getProperties();
		return properties.getProperty(propertyName);
	}

	public static List<String> createList(final String[] array) {
		final List<String> list = new ArrayList<String>();
		if (array != null) {
			for (final String element : array) {
				list.add(element);
			}
		}
		return list;
	}

	public List<Dependency> getDependencyListFromConfiguration(final Xpp3Dom configuration, final String name) {
		final List<Dependency> dependencies = new ArrayList<Dependency>();
		if (configuration != null) {
			final Xpp3Dom child = configuration.getChild(name);
			for (int i = 0; i < child.getChildCount(); i++) {
				final Xpp3Dom excludeDependency = child.getChild(i);
				final Dependency dependency = new Dependency();
				for (int j = 0; j < excludeDependency.getChildCount(); j++) {
					final Xpp3Dom xpp3Dom = excludeDependency.getChild(j);
					if (xpp3Dom.getName().equals("groupId")) {
						dependency.setGroupId(xpp3Dom.getValue());
					}
					if (xpp3Dom.getName().equals("artifactId")) {
						dependency.setArtifactId(xpp3Dom.getValue());
					}
					if (xpp3Dom.getName().equals("version")) {
						dependency.setVersion(xpp3Dom.getValue());
					}
					if (xpp3Dom.getName().equals("classifier")) {
						dependency.setClassifier(xpp3Dom.getValue());
					}
					if (xpp3Dom.getName().equals("type")) {
						dependency.setType(xpp3Dom.getValue());
					}
				}
				dependencies.add(dependency);
			}
		}
		return dependencies;
	}

}
