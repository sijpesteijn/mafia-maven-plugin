package nl.sijpesteijn.testing.fitnesse.plugins.context;

import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaException;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.Project;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * FitNesseContextWriter.
 */
public class FitNesseContextWriter {

    /**
     * Define return string.
     */
    private static final String DEF_RETURN = "\n";

    /**
     * Define path string.
     */
    private static final String DEF_PATH = "!path ";

    /**
     * Project.
     */
    private final Project project;

    /**
     * List of statics.
     */
    private final List<String> statics;

    /**
     * List of targets.
     */
    private final List<String> targets;

    /**
     * List of resources.
     */
    private final List<String> resources;

    /**
     * List of exclude dependencies.
     */
    private final List<Dependency> excludeDependencies;

    /**
     * Content file.
     */
    private File content;

    /**
     * Indicates if to use plugin dependencies.
     */
    private boolean addPluginDependencies;

    /**
     * Constructor.
     *
     * @param project               - the mafia project.
     * @param statics               - list of statics to add.
     * @param targets               - list of targets to add.
     * @param resources             - list of resources to add.
     * @param excludeDependencies   - list of exclude dependencies.
     * @param content               - the content file.
     * @param addPluginDependencies - add the plugin dependencies.
     */
    public FitNesseContextWriter(final Project project, final List<String> statics, final List<String> targets,
                                 final List<String> resources, final List<Dependency> excludeDependencies,
                                 final File content, final boolean addPluginDependencies) {
        this.project = project;
        this.statics = statics;
        this.targets = targets;
        this.resources = resources;
        this.excludeDependencies = excludeDependencies;
        this.content = content;
        this.addPluginDependencies = addPluginDependencies;
    }

    /**
     * Write the content file.
     *
     * @throws MafiaException - unable to write content.txt.
     */
    public final void writeContent() throws MafiaException {
        if (!content.exists()) {
            content.mkdirs();
        }
        try {
            FileOutputStream fos = new FileOutputStream(new File(content.getAbsolutePath()
                    + File.separator + "content.txt"));
            Writer writer = new OutputStreamWriter(fos, "UTF-8");
            addPluginInfo(writer);
            addLines(writer, "Statics:", statics, "", "");
            addLines(writer, "Resources:", resources, DEF_PATH, "");
            addLines(writer, "Targets:", targets, DEF_PATH, "/target/classes/");
            addDependencies(writer);
            if (addPluginDependencies) {
                addPluginDependencies(writer);
            }
            writer.close();
            fos.close();
        } catch (IOException e) {
            throw new MafiaException("Failed to write content.txt file.", e);
        }
    }

    /**
     * Add the plugin dependencies.
     *
     * @param writer - the writer.
     * @throws MafiaException - some mafia exception.
     * @throws IOException    - unable to write to content file.
     */
    private void addPluginDependencies(final Writer writer) throws MafiaException, IOException {
        writer.write("!note Plugin Dependencies:" + DEF_RETURN + DEF_RETURN);
        List<Dependency> pluginDependencies = project.getPluginDependencies();
        if (pluginDependencies != null) {
            for (Dependency pluginDependency : pluginDependencies) {
                if (!isExcludeDependency(pluginDependency)) {
                    writer.write("!path " + project.resolveDependency(pluginDependency) + DEF_RETURN);
                }
            }
        }
        writer.write(DEF_RETURN);
    }

    /**
     * Add the project dependencies.
     *
     * @param writer - the writer.
     * @throws IOException    - unable to content file.
     * @throws MafiaException - some mafia exception.
     */
    private void addDependencies(final Writer writer) throws IOException, MafiaException {
        writer.write("!note Project Dependencies:" + DEF_RETURN + DEF_RETURN);
        Set<Artifact> artifacts = project.getArtifacts();
        for (Artifact artifact : artifacts) {
            if (!isExcludeDependency(createDependency(artifact))) {
                writer.write("!path " + project.resolveArtifact(artifact) + DEF_RETURN);
            }
        }
        writer.write(DEF_RETURN);
    }

    /**
     * Create a dependency out of artifact.
     *
     * @param artifact - the artifact.
     * @return a dependency.
     */
    private Dependency createDependency(final Artifact artifact) {
        Dependency dependency = new Dependency();
        dependency.setGroupId(artifact.getGroupId());
        dependency.setArtifactId(artifact.getArtifactId());
        dependency.setVersion(artifact.getVersion());
        dependency.setType(artifact.getType());
        dependency.setScope(artifact.getScope());
        dependency.setClassifier(artifact.getClassifier());
        return dependency;
    }

    /**
     * Check if the dependency should be included.
     *
     * @param toAddDependency - a dependency.
     * @return boolean.
     */
    private boolean isExcludeDependency(final Dependency toAddDependency) {
        if (excludeDependencies != null) {
            for (Dependency dependency : excludeDependencies) {
                if (sameDependency(toAddDependency, dependency)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if dependencies are the same.
     *
     * @param dep1 - first dependency
     * @param dep2 - second dependency
     * @return - equal
     */
    private boolean sameDependency(final Dependency dep1, final Dependency dep2) {
        if (!StringUtils.equals(dep1.getGroupId(), dep2.getGroupId())) {
            return false;
        }
        if (!StringUtils.equals(dep1.getArtifactId(), dep2.getArtifactId())) {
            return false;
        }
        if (!StringUtils.equals(dep1.getVersion(), dep2.getVersion())) {
            return false;
        }
        if (!StringUtils.equals(dep1.getClassifier(), dep2.getClassifier())) {
            return false;
        }
        if (!StringUtils.equals(dep1.getScope(), dep2.getScope())) {
            return false;
        }
        if (!StringUtils.equals(dep1.getType(), dep2.getType())) {
            return false;
        }
        return true;
    }

    /**
     * Add the plugin information.
     *
     * @param writer - the writer.
     * @throws IOException - unable to write content file.
     */
    private void addPluginInfo(final Writer writer) throws IOException {
        writer.write("# File created by Mafia plugin on " + new Date().toString()
                + DEF_RETURN);
        writer.write("# More info: https://github.com/sijpesteijn/mafia-maven-plugin"
                + DEF_RETURN);
        writer.write(DEF_RETURN);
    }

    /**
     * Add lines to the content.txt file.
     *
     * @param writer  - the writer
     * @param chapter - Chapter title.
     * @param lines   - the lines.
     * @param prefix  - prefix.
     * @param postfix - postfix
     * @throws IOException - unable to write content file.
     */
    private void addLines(final Writer writer, final String chapter, final List<String> lines,
                          final String prefix, final String postfix) throws IOException {
        if (lines != null && !lines.isEmpty()) {
            writer.write("!note " + chapter + DEF_RETURN + DEF_RETURN);
            for (final String entry : lines) {
                writer.write(prefix + entry + postfix + DEF_RETURN);
            }
            writer.write(DEF_RETURN);
        }
    }
}
