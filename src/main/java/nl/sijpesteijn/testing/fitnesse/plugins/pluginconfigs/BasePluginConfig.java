package nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs;

import org.apache.maven.plugin.logging.Log;

public class BasePluginConfig {
	private final String wikiRoot;
	private final String nameRootPage;
	private final String repositoryDirectory;
	private final Log mavenLogger;
	private final String logDirectory;
	private final int retainDays;
	private final int fitnessePort;

	public BasePluginConfig(final String wikiRoot, final String nameRootPage,
			final String repositoryDirectory, final String logDirectory,
			final int fitnessePort, final int retainDays, final Log mavenLogger) {
		this.wikiRoot = wikiRoot;
		this.nameRootPage = nameRootPage;
		this.repositoryDirectory = repositoryDirectory;
		this.logDirectory = logDirectory;
		this.fitnessePort = fitnessePort;
		this.retainDays = retainDays;
		this.mavenLogger = mavenLogger;
	}

	public String getWikiRoot() {
		return wikiRoot;
	}

	public String getNameRootPage() {
		return nameRootPage;
	}

	public String getRepositoryDirectory() {
		return repositoryDirectory;
	}

	public String getLogDirectory() {
		return logDirectory;
	}

	public int getRetainDays() {
		return retainDays;
	}

	public int getFitnessePort() {
		return fitnessePort;
	}

	public Log getMavenLogger() {
		return mavenLogger;
	}
}
