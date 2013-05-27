package nl.sijpesteijn.testing.fitnesse.plugins;

import nl.sijpesteijn.testing.fitnesse.plugins.context.FitNesseContextWriter;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaException;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.Project;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.List;

/**
 * This mojo will generate a new content.txt (classpath) file for FitNesse
 * The file is default placed in the wiki root of FitNesse.
 */
@Mojo(name = "content", defaultPhase = LifecyclePhase.POST_CLEAN)
public class FitNesseContentMojo extends AbstractFitNesseMojo {

    /**
     * List of static entries to add to content.txt.
     */
    @Parameter(property = "statics")
    private List<String> statics;

    /**
     * List of target directories to add to content.txt Each target is resolved.
     */
    @Parameter(property = "targets")
    private List<String> targets;

    /**
     * List of resource entries to add to content.txt.
     */
    @Parameter(property = "resources")
    private List<String> resources;

    /**
     * List of dependencies to exclude from the content.txt.
     */
    @Parameter(property = "excludeDependencies")
    private List<Dependency> excludeDependencies;

    /**
     * Add plugin dependencies.
     */
    @Parameter(property = "addPluginDependencies", defaultValue = "true")
    private boolean addPluginDependencies;

    /**
     * {@inheritDoc}
     */
    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {
        getLog().debug(toString());
        final File contentPath = new File(getContentPath());
        final Project project = getMafiaProject();
        final FitNesseContextWriter fitNesseContentWriter = new FitNesseContextWriter(project, statics,
                targets, resources, excludeDependencies, contentPath, addPluginDependencies);
        try {
            fitNesseContentWriter.writeContent();
            getLog().info("FitNesse environment written to " + contentPath + "/content.txt");
        } catch (MafiaException e) {
            throw new MojoExecutionException("Could not write FitNesse enviroment to " + contentPath, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return super.toString() + ", Statics: " + statics + ", Targets: " + targets
                + ", Resources: " + resources + ", Exclude dependencies: " + excludeDependencies;
    }

    /**
     * Get the location of the FitNesse content.txt file.
     *
     * @return {@link java.lang.String}
     */
    public final String getContentPath() {
        String contentPath = getWikiRoot();
        if (getWikiRoot().endsWith(File.separator)) {
            contentPath += File.separator;
        }
        contentPath += getNameRootPage() + File.separator;
        return contentPath;
    }
}
