package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import java.io.File;

public class FileUtils {

	public static String formatPath(final String resolveDependencyPath) {
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			return "\"" + resolveDependencyPath + "\"";
		} else {
			return resolveDependencyPath.replaceAll(" ", "\\ ");
		}

	}

	public static boolean deleteRecursively(final File file) {
		if (file.exists()) {
			final File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteRecursively(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (file.delete());
	}

}
