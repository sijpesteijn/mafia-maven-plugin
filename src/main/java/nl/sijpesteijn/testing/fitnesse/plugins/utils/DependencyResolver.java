package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;

public class DependencyResolver {

    /**
     * 
     * @return {@link java.lang.String}
     * @throws Exception
     *         {@link java.lang.Exception}
     */
    public String getJarLocation(final List<Dependency> dependencies, final String part, final String baseDir)
            throws MojoExecutionException
    {
        for (int i = 0; i < dependencies.size(); i++) {
            final Dependency dependency = dependencies.get(i);
            final String resolveDependencyPath = resolveDependencyPath(dependency, baseDir);
            if (resolveDependencyPath.contains(part)) {
                return resolveDependencyPath;
            }
        }
        throw new MojoExecutionException("Could not find jar location.");
    }

    public String resolveDependencyPath(final Dependency dependency, final String baseDir) {
        String resolved = baseDir.replace("\\", "/") + "/" + dependency.getGroupId().replace(".", "/") + "/";
        resolved += dependency.getArtifactId() + "/" + dependency.getVersion() + "/";
        resolved += dependency.getArtifactId() + "-" + dependency.getVersion();
        if (dependency.getClassifier() != null && !dependency.getClassifier().equals("")) {
            resolved += "-" + dependency.getClassifier();
        }
        resolved += "." + dependency.getType();
        return resolved;
    }

}
