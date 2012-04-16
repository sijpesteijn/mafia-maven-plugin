package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import org.apache.maven.plugin.logging.Log;

public class FitNesseComanderConfig {

    private String testResultsDirectoryName;
    private String nameRootPage;
    private int fitNessePort;
    private int retainDays;
    private String logDirectory;
    private String rootPath;
    private Log mavenLogger;

    public int getFitNessePort() {
        return fitNessePort;
    }

    public String getRootPath() {
        return rootPath;
    }

    public String getNameRootPage() {
        return nameRootPage;
    }

    public String getLogDirectory() {
        return logDirectory;
    }

    public String getTestResultsDirectoryName() {
        return testResultsDirectoryName;
    }

    public int getRetainDays() {
        return retainDays;
    }

    public void setTestResultsDirectoryName(final String testResultsDirectoryName) {
        this.testResultsDirectoryName = testResultsDirectoryName;
    }

    public void setNameRootPage(final String nameRootPage) {
        this.nameRootPage = nameRootPage;
    }

    public void setFitNessePort(final int fitNessePort) {
        this.fitNessePort = fitNessePort;
    }

    public void setRetainDays(final int retainDays) {
        this.retainDays = retainDays;
    }

    public void setWikiRoot(final String rootPath) {
        this.rootPath = rootPath;
    }

    public void setLogDirectory(final String logDirectory) {
        this.logDirectory = logDirectory;
    }

    public Log getMavenLogger() {
        return mavenLogger;
    }

    public void setMavenLogger(final Log mavenLogger) {
        this.mavenLogger = mavenLogger;
    }
}
