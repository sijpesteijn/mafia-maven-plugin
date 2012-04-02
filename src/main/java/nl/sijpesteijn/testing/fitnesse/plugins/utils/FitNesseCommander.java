package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;

import fitnesse.ComponentFactory;
import fitnesse.FitNesse;
import fitnesse.FitNesseContext;
import fitnesse.VelocityFactory;
import fitnesse.WikiPageFactory;
import fitnesse.authentication.PromiscuousAuthenticator;
import fitnesse.components.Logger;
import fitnesse.html.HtmlPageFactory;
import fitnesse.responders.ResponderFactory;
import fitnesse.responders.WikiImportTestEventListener;
import fitnesse.responders.run.TestSummary;
import fitnesse.wiki.PageType;
import fitnesse.wiki.PageVersionPruner;

public class FitNesseCommander {

    private final FitNesseComanderConfig fitNesseCommanderConfig;
    private FitNesse fitnesse;
    private final TestSummary summary = new TestSummary();

    public FitNesseCommander(final FitNesseComanderConfig fitNesseCommanderConfig) {
        this.fitNesseCommanderConfig = fitNesseCommanderConfig;
    }

    public boolean start() throws MojoFailureException {
        final FitNesseContext context = loadContext();
        VelocityFactory.makeVelocityFactory(context);
        PageVersionPruner.daysTillVersionsExpire = fitNesseCommanderConfig.getRetainDays();
        fitnesse = new FitNesse(context);
        return fitnesse.start();
    }

    public void stop() throws Exception {
        fitnesse.stop();
    }

    private FitNesseContext loadContext() throws MojoFailureException {
        final FitNesseContext context = new FitNesseContext();
        context.port = fitNesseCommanderConfig.getFitNessePort();
        context.rootPath = fitNesseCommanderConfig.getRootPath();
        final ComponentFactory componentFactory = new ComponentFactory(context.rootPath);
        context.rootDirectoryName = fitNesseCommanderConfig.getNameRootPage();
        context.setRootPagePath();
        final String defaultNewPageContent = componentFactory.getProperty(ComponentFactory.DEFAULT_NEWPAGE_CONTENT);
        if (defaultNewPageContent != null)
            context.defaultNewPageContent = defaultNewPageContent;
        final WikiPageFactory wikiPageFactory = new WikiPageFactory();
        context.responderFactory = new ResponderFactory(context.rootPagePath);
        final String logDirectory = fitNesseCommanderConfig.getLogDirectory();
        context.logger = logDirectory != null ? new Logger(logDirectory) : null;
        context.authenticator = new PromiscuousAuthenticator();
        try {
            context.htmlPageFactory = componentFactory.getHtmlPageFactory(new HtmlPageFactory());
            context.testResultsDirectoryName = fitNesseCommanderConfig.getTestResultsDirectoryName();
            context.root = wikiPageFactory.makeRootPage(context.rootPath, context.rootDirectoryName, componentFactory);
        } catch (final Exception e) {
            throw new MojoFailureException("Could not create fitnesse context", e);
        }
        WikiImportTestEventListener.register();

        return context;
    }

    public TestSummary runTest(final String testName) throws MojoExecutionException {
        callUrl(getTestUrl(testName, PageType.TEST, null));
        return summary;
    }

    public TestSummary runTestSuite(final String suiteName) throws MojoExecutionException {
        callUrl(getTestUrl(suiteName, PageType.SUITE, null));
        return summary;
    }

    public TestSummary runByTagFilter(final String suiteFilter, final String suitePageName)
            throws MojoExecutionException
    {
        callUrl(getTestUrl(suitePageName, PageType.SUITE, suiteFilter));
        return summary;
    }

    private void callUrl(final String testUrl) throws MojoExecutionException {
        InetAddress addr;
        try {
            addr = InetAddress.getLocalHost();
            final URL url = new URL("http", addr.getHostAddress(), fitNesseCommanderConfig.getFitNessePort(), testUrl);
            final URLConnection yc = url.openConnection();
            final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.contains("getElementById(\"test-summary\")")
                        && inputLine.contains("<strong>Assertions:</strong>"))
                {
                    updateSummary(inputLine);
                }
            }
            in.close();
        } catch (final UnknownHostException e) {
            throw new MojoExecutionException("Could not make url call", e);
        } catch (final MalformedURLException e) {
            throw new MojoExecutionException("Could not make url call", e);
        } catch (final FileNotFoundException e) {
            throw new MojoExecutionException("Could not make url call", e);
        } catch (final IOException e) {
            throw new MojoExecutionException("Could not make url call", e);
        }
    }

    private void updateSummary(final String inputLine) {
        final String assertions = "<strong>Assertions:</strong>";
        int start = inputLine.indexOf(assertions);
        int stop = inputLine.indexOf("right", start + assertions.length());
        final String rightStr = inputLine.substring(start + assertions.length(), stop);
        start = stop + "right".length() + 1;
        stop = inputLine.indexOf("wrong", start);
        final String wrongStr = inputLine.substring(start, stop);
        start = stop + "wrong".length() + 1;
        stop = inputLine.indexOf("ignored", start);
        final String ignoreStr = inputLine.substring(start, stop);
        start = stop + "ignored".length() + 1;
        stop = inputLine.indexOf("exceptions", start);
        final String exceptionsStr = inputLine.substring(start, stop);
        summary.right += Integer.parseInt(rightStr.trim());
        summary.wrong += Integer.parseInt(wrongStr.trim());
        summary.ignores += Integer.parseInt(ignoreStr.trim());
        summary.exceptions += Integer.parseInt(exceptionsStr.trim());
    }

    private String getTestUrl(final String pageName, final PageType pageType, final String suiteFilter) {
        if (suiteFilter != null)
            return "/" + pageName + "?responder=suite&suiteFilter=" + suiteFilter;
        else
            return "/" + pageName + "?" + pageType.name().toString().toLowerCase();
    }

    public void clearTestResultsDirectory() throws MojoExecutionException {
        final String directoryName =
                fitNesseCommanderConfig.getRootPath() + File.separatorChar + fitNesseCommanderConfig.getNameRootPage()
                        + File.separatorChar + "files" + File.separatorChar
                        + fitNesseCommanderConfig.getTestResultsDirectoryName();
        try {
            FileUtils.deleteDirectory(directoryName);
        } catch (final IOException e) {
            throw new MojoExecutionException("Could not delete directory: " + directoryName, e);
        }
    }

    public TestSummary getTestSummary() {
        return this.summary;
    }

}
