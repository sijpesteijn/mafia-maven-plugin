package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Dependency;

public class LogUtils {

    public static String getString(final List<String> elements) {
        if (elements.isEmpty()) {
            return "[none]";
        }
        return StringUtils.join(elements, ",");
    }

    public static String getStringFromDependencies(final List<Dependency> dependencies) {
        String list = "[none]";
        if (!dependencies.isEmpty()) {
            for (final Dependency dependency : dependencies) {
                list +=
                        dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion()
                                + ", ";
            }
            list = list.substring(0, list.length() - 2);
        }
        return list;
    }
}
