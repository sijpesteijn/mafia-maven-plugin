package nl.sijpesteijn.testing.fitnesse.plugins.managers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.StarterPluginConfig;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.CommandRunner;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.DependencyResolver;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.FirstTimeWriter;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;

public class StarterPluginManager implements PluginManager {

    private final StarterPluginConfig pluginConfig;
    private final DependencyResolver resolver;

    public StarterPluginManager(final StarterPluginConfig pluginConfig, final DependencyResolver resolver) {
        this.pluginConfig = pluginConfig;
        this.resolver = resolver;
    }

    @Override
    public void run() throws MojoExecutionException {
        String jarLocation;
        jarLocation =
                resolver.getJarLocation(pluginConfig.getDependencies(), "org/fitnesse", pluginConfig.getBaseDir());
        final String jvmArgumentsString = getJVMArguments(pluginConfig.getJvmArguments());
        final String dependencyList = getDependencyList();
        final String command =
                "java"
                        + jvmArgumentsString
                        + " -cp "
                        + jarLocation
                        + File.pathSeparatorChar
                        + (dependencyList + " fitnesseMain.FitNesseMain -p " + pluginConfig.getFitNessePort() + " -d "
                                + pluginConfig.getWikiRoot() + " -r " + pluginConfig.getNameRootPage() + " -l "
                                + pluginConfig.getLogPath() + " -e " + pluginConfig.getRetainDays());

        final CommandRunner runner = new CommandRunner();
        try {
            runner.start(command);
            if (runner.waitForSetupToFinish() && runner.errorBufferContains("patient.")) {
                new FirstTimeWriter(pluginConfig.getNameRootPage());
            }
        } catch (final IOException e) {
            throw new MojoExecutionException("Could not start fitnesse.", e);
        } catch (final InterruptedException e) {
            throw new MojoExecutionException("Could not start fitnesse.", e);
        }
    }

    private String getDependencyList() {
        if (pluginConfig.getJvmDependencies() == null) {
            return "";
        }
        String list = "";
        for (final Dependency dependency : pluginConfig.getJvmDependencies()) {
            final String dependencyPath = resolver.resolveDependencyPath(dependency, pluginConfig.getBaseDir());
            if (!dependencyPath.trim().equals("")) {
                list += dependencyPath;
            }
        }
        return list;
    }

    private String getJVMArguments(final List<String> arguments) {
        if (arguments == null) {
            return "";
        }
        String list = "";
        for (final String argument : arguments) {
            list += "-D" + argument + " ";
        }
        return list;
    }

}
