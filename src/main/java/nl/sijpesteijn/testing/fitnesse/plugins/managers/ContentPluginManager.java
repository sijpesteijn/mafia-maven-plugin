package nl.sijpesteijn.testing.fitnesse.plugins.managers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.ContentPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.DependencyResolver;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * 
 * This plugin manager will create and fill the content.txt file for FitNesse. This content.txt file (root page) will
 * hold all defined variables & classpath entries.
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
     *        {@link nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.ContentPluginConfig}
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

            addEntries("Statics", contentPluginConfig.getStatics(), "");
            addEntries("Resources", contentPluginConfig.getResources(), "");
            addEntries("Targets", contentPluginConfig.getTargets(), "/target/classes/");

            addCompileClasspathElements();
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

    /**
     * Creates the content.txt file in the location as set in the plugin configuration.
     * 
     * @return {@link java.io.File}
     */
    private File createContentFile() {
        final File contentDirectory =
                new File(contentPluginConfig.getWikiRoot() + "/" + contentPluginConfig.getNameRootPage());
        if (!contentDirectory.exists()) {
            contentDirectory.mkdirs();
        }

        final File content = new File(contentDirectory, "content.txt");
        return content;
    }

    /**
     * Add the compile classpath dependencies to the content.txt. Dependencies are searched in the local repository
     * 
     * @throws IOException
     */
    @SuppressWarnings("rawtypes")
    private void addCompileClasspathElements() throws IOException {
        final List elements = contentPluginConfig.getCompileClasspathElements();
        w.write("Class Dependencies:" + DEF_RETURN);
        if (elements != null) {
            for (int i = 0; i < elements.size(); i++) {
                final String element = elements.get(i).toString().replace('\\', '/');
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
     *        {@link java.lang.String}
     * @return {@link boolean}
     * @throws IOException
     */
    private boolean isExcludeDependency(final String classpathElement) {
        if (classpathElement.endsWith("classes")) {
            return false;
        }
        if (contentPluginConfig.getExcludeDependencies() != null) {
            for (final Dependency excludeDependency : contentPluginConfig.getExcludeDependencies()) {
                final String dependencyPath =
                        resolver.resolveDependencyPath(excludeDependency, contentPluginConfig.getBasedir());
                if (dependencyPath.equals(classpathElement)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Add entries to the content.txt file.
     * 
     * @param part
     *        {@link java.lang.String}
     * @param entries
     *        {@link java.util.List}
     * @param suffix
     * @throws IOException
     */
    private void addEntries(final String part, final List<String> entries, final String suffix) throws IOException {
        if (entries != null && !entries.isEmpty()) {
            w.write(part + ":" + DEF_RETURN);
            for (final String entry : entries) {
                w.write(DEF_PATH + entry + suffix + DEF_RETURN);
            }
        }
    }
}
