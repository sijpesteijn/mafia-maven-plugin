package nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs;

import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;

public class BasePluginConfig {
	private String wikiRoot = ".";
	private String nameRootPage = "FitNesseRoot";
	private final String repositoryDirectory;
	private final Log mavenLogger;
	private String logDirectory = "./log";
	private int retainDays = 0;
	private int fitnessePort = 9090;
	private String mafiaTestResultsDirectory = "mafiaTestResults";
	private final List<Dependency> dependencies;

	public BasePluginConfig(final String wikiRoot, final String nameRootPage, final String repositoryDirectory,
			final String logDirectory, final List<Dependency> dependencies, final Log mavenLogger) {
		this.wikiRoot = wikiRoot;
		this.nameRootPage = nameRootPage;
		this.repositoryDirectory = repositoryDirectory;
		this.logDirectory = logDirectory;
		this.dependencies = dependencies;
		this.mavenLogger = mavenLogger;
	}

	public BasePluginConfig(final String wikiRoot, final String nameRootPage, final String repositoryDirectory,
			final String logDirectory, final int fitnessePort, final int retainDays,
			final List<Dependency> dependencies, final Log mavenLogger) {
		this.wikiRoot = wikiRoot;
		this.nameRootPage = nameRootPage;
		this.repositoryDirectory = repositoryDirectory;
		this.logDirectory = logDirectory;
		this.fitnessePort = fitnessePort;
		this.retainDays = retainDays;
		this.dependencies = dependencies;
		this.mavenLogger = mavenLogger;
	}

	public BasePluginConfig(final String wikiRoot, final String nameRootPage, final String repositoryDirectory,
			final String logDirectory, final int fitnessePort, final int retainDays,
			final String mafiaTestResultsDirectory, final List<Dependency> dependencies, final Log mavenLogger) {
		this.wikiRoot = wikiRoot;
		this.nameRootPage = nameRootPage;
		this.repositoryDirectory = repositoryDirectory;
		this.logDirectory = logDirectory;
		this.fitnessePort = fitnessePort;
		this.retainDays = retainDays;
		this.mafiaTestResultsDirectory = mafiaTestResultsDirectory;
		this.dependencies = dependencies;
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

	public String getMafiaTestResultsDirectory() {
		return mafiaTestResultsDirectory;
	}

	public List<Dependency> getDependencies() {
		return dependencies;
	}
}
