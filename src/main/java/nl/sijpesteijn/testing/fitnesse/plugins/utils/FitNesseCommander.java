package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;

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
    private final FitNesse fitnesse;
    private final TestSummary summary = new TestSummary();

    public FitNesseCommander(final FitNesseComanderConfig fitNesseCommanderConfig) throws MojoFailureException {
        this.fitNesseCommanderConfig = fitNesseCommanderConfig;
        final FitNesseContext context = loadContext();
        VelocityFactory.makeVelocityFactory(context);
        PageVersionPruner.daysTillVersionsExpire = fitNesseCommanderConfig.getRetainDays();
        fitnesse = new FitNesse(context);
    }

    public void start() throws MojoFailureException {
        fitnesse.start();
        if (!fitnesse.isRunning()) {
            throw new MojoFailureException("Could not start fitnesse.");
        }
    }

    public void stop() throws MojoFailureException {
        try {
            fitnesse.stop();
        } catch (final Exception e) {
            throw new MojoFailureException("Could not stop fitnesse.", e);
        }
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
        context.logger = getLogger();
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

    private Logger getLogger() {
        final String logDirectory = fitNesseCommanderConfig.getLogDirectory();
        if (logDirectory != null) {
            createDirIfNotExists(logDirectory);
            return new Logger(logDirectory);
        }
        return null;
    }

    private void createDirIfNotExists(final String logDirectory) {
        final File logDir = new File(logDirectory);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
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
        String ipAddress = "";
        URL url = null;
        try {
            ipAddress = getIpAddress();
            url = new URL("http", ipAddress, fitNesseCommanderConfig.getFitNessePort(), testUrl);
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
            throw new MojoExecutionException("Host " + ipAddress + " not found", e);
        } catch (final MalformedURLException e) {
            throw new MojoExecutionException("Could not make url call", e);
        } catch (final FileNotFoundException e) {
            throw new MojoExecutionException("Url not available: " + url.toString(), e);
        } catch (final IOException e) {
            throw new MojoExecutionException("Could not make url call", e);
        }
    }

    private String getIpAddress() throws SocketException, UnknownHostException {
        final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        for (final NetworkInterface networkInterface : Collections.list(networkInterfaces)) {
            if (!networkInterface.isLoopback()) {
                final Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                for (final InetAddress inetAddress : Collections.list(inetAddresses)) {
                    if (inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        }
        return null;
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
