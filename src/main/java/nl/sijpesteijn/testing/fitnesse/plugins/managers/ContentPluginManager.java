package nl.sijpesteijn.testing.fitnesse.plugins.managers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.ContentPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.DependencyResolver;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * 
 * This plugin manager will create and fill the content.txt file for FitNesse.
 * This content.txt file (root page) will hold all defined variables & classpath
 * entries.
 * 
 */
public class ContentPluginManager implements PluginManager {
    private static final String DEF_RETURN = "\n";
    private static final String DEF_PATH = "!path ";
    private final ContentPluginConfig contentPluginConfig;
    private final DependencyResolver resolver = new DependencyResolver();
    private FileWriter w;

    /**
     * 
     * @param contentPluginConfig
     *            {@link nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.ContentPluginConfig}
     */
    public ContentPluginManager(final ContentPluginConfig contentPluginConfig) {
        this.contentPluginConfig = contentPluginConfig;
    }

    /**
     * Generate the content.txt file for FitNesse.
     */
    @Override
    public void run() throws MojoFailureException, MojoExecutionException {
        final File content = createContentFile();
        try {
            w = new FileWriter(content);

            addPluginInfo();
            addLines("Statics", contentPluginConfig.getStatics(), "", "");
            addLines("Resources", contentPluginConfig.getResources(), DEF_PATH, "");
            addLines("Targets", contentPluginConfig.getTargets(), DEF_PATH, "/target/classes/");

            addCompileClasspathElements();
            addWarDependencies();
        } catch (final IOException e) {
            throw new MojoExecutionException("Error creating file " + content, e);
        } finally {
            if (w != null) {
                try {
                    w.close();
                } catch (final IOException e) {
                    System.out.println("Could not close content file");
                }
            }
        }

    }

    private void addWarDependencies() throws IOException {
        w.write("!note War dependencies:" + DEF_RETURN);
        final Map<String, Artifact> all = contentPluginConfig.getArtifactMap();
        final List<Artifact> warArtifacts = new ArrayList<Artifact>();
        if (all != null) {
            for (final String artifactId : all.keySet()) {
                final Artifact artifact = all.get(artifactId);
                if (artifact.getType().equals("war")) {
                    warArtifacts.add(artifact);
                }
            }
        }

        for (final Artifact artifact : warArtifacts) {
            w.write(DEF_PATH + artifact.getFile().toString().replace('\\', '/') + DEF_RETURN);
        }
    }

    private void addPluginInfo() throws IOException {
        w.write("# File created by Mafia plugin on " + new Date().toString() + DEF_RETURN);
        w.write("# Version: 1.0.0" + DEF_RETURN);
        w.write("# More info: https://github.com/sijpesteijn/mafia-maven-plugin" + DEF_RETURN);
        w.write(DEF_RETURN);
    }

    /**
     * Creates the content.txt file in the location as set in the plugin
     * configuration.
     * 
     * @return {@link java.io.File}
     */
    private File createContentFile() {
        final File contentDirectory = new File(contentPluginConfig.getWikiRoot() + "/"
                + contentPluginConfig.getNameRootPage());
        if (!contentDirectory.exists()) {
            contentDirectory.mkdirs();
        }

        final File content = new File(contentDirectory, "content.txt");
        return content;
    }

    /**
     * Add the compile classpath dependencies to the content.txt. Dependencies
     * are searched in the local repository
     * 
     * @throws IOException
     */
    @SuppressWarnings("rawtypes")
    private void addCompileClasspathElements() throws IOException {
        final List classpathElements = contentPluginConfig.getCompileClasspathElements();
        if (classpathElements != null) {
            w.write("!note Class Dependencies:" + DEF_RETURN);
            Collections.sort(classpathElements);
            for (int i = 0; i < classpathElements.size(); i++) {
                final String element = classpathElements.get(i).toString().replace('\\', '/');
                if (!isExcludeDependency(element)) {
                    if (element.endsWith("classes")) {
                        w.write(DEF_PATH + element + "/" + DEF_RETURN);
                    } else {
                        w.write(DEF_PATH + element + DEF_RETURN);
                    }
                }
            }
        }
    }

    /**
     * Check if the dependency is not part of the list of excluded dependencies.
     * 
     * @param classpathElement
     *            {@link java.lang.String}
     * @return {@link boolean}
     * @throws IOException
     */
    private boolean isExcludeDependency(final String classpathElement) {
        if (classpathElement.endsWith("classes")) {
            return false;
        }
        if (contentPluginConfig.getExcludeDependencies() != null) {
            for (final Dependency excludeDependency : contentPluginConfig.getExcludeDependencies()) {
                final String dependencyPath = resolver.resolveDependencyPath(excludeDependency,
                        contentPluginConfig.getBasedir());
                if (dependencyPath.equals(classpathElement)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Add lines to the content.txt file.
     * 
     * @param chapter
     *            {@link java.lang.String}
     * @param lines
     *            {@link java.util.List}
     * @param suffix
     * @throws IOException
     */
    private void addLines(final String chapter, final List<String> lines, final String prefix, final String suffix)
            throws IOException {
        if (lines != null && !lines.isEmpty()) {
            w.write("!note " + chapter + ":" + DEF_RETURN);
            for (final String entry : lines) {
                w.write(prefix + entry + suffix + DEF_RETURN);
            }
            w.write(DEF_RETURN);
        }
    }
}
