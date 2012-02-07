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

public class ContentPluginManager implements PluginManager {
    private static final String DEF_RETURN = "\n";
    private static final String DEF_PATH = "!path ";
    private final ContentPluginConfig pluginConfig;
    private final DependencyResolver resolver;

    public ContentPluginManager(final ContentPluginConfig pluginConfig, final DependencyResolver resolver) {
        this.pluginConfig = pluginConfig;
        this.resolver = resolver;
    }

    @Override
    public void run() throws MojoFailureException, MojoExecutionException {
        final File contentDirectory = new File(pluginConfig.getWikiRoot() + "/" + pluginConfig.getNameRootPage());
        if (!contentDirectory.exists()) {
            contentDirectory.mkdirs();
        }

        final File content = new File(contentDirectory, "content.txt");
        FileWriter w = null;
        try {
            w = new FileWriter(content);

            addEntries(w, "Statics", pluginConfig.getStatics());
            addEntries(w, "Resources", pluginConfig.getResources());
            addEntries(w, "Targets", pluginConfig.getTargets());

            addCompileClasspathElements(w);
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

    @SuppressWarnings("rawtypes")
    private void addCompileClasspathElements(final FileWriter w) throws IOException {
        final List elements = pluginConfig.getCompileClasspathElements();
        w.write("Class Dependencies:" + DEF_RETURN);
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

    /**
     * 
     * @param classpathElement
     *        {@link java.lang.String}
     * @return
     * @throws IOException
     */
    private boolean isExcludeDependency(final String classpathElement) {
        if (classpathElement.endsWith("classes")) {
            return false;
        }
        if (pluginConfig.getExcludeDependencies() != null) {
            for (final Dependency excludeDependency : pluginConfig.getExcludeDependencies()) {
                final String dependencyPath =
                        resolver.resolveDependencyPath(excludeDependency, pluginConfig.getBasedir());
                if (dependencyPath.equals(classpathElement)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void addEntries(final FileWriter w, final String part, final List<String> entries) throws IOException {
        if (entries != null && !entries.isEmpty()) {
            w.write(part + ":" + DEF_RETURN);
            for (final String entry : entries) {
                w.write(DEF_PATH + entry + DEF_RETURN);
            }
        }
    }
}
