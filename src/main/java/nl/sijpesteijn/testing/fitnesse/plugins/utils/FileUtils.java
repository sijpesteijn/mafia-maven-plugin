package nl.sijpesteijn.testing.fitnesse.plugins.utils;

public class FileUtils {

	public static String formatPath(final String resolveDependencyPath) {
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			return "\'" + resolveDependencyPath + "\'";
		} else {
			return resolveDependencyPath.replaceAll(" ", "\\ ");
		}

	}

}
