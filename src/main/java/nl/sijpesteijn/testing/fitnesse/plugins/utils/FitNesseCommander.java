package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import org.apache.maven.plugin.MojoExecutionException;
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
import fitnesse.wiki.PageType;
import fitnesse.wiki.PageVersionPruner;

public class FitNesseCommander {

	private final FitNesseComanderConfig fitNesseCommanderConfig;
	private FitNesse fitnesse;

	public FitNesseCommander(final FitNesseComanderConfig fitNesseCommanderConfig) {
		this.fitNesseCommanderConfig = fitNesseCommanderConfig;
	}

	public boolean start() throws Exception {
		final FitNesseContext context = loadContext();
		VelocityFactory.makeVelocityFactory(context);
		PageVersionPruner.daysTillVersionsExpire = fitNesseCommanderConfig.getRetainDays();
		fitnesse = new FitNesse(context);
		return fitnesse.start();
	}

	public void stop() throws Exception {
		fitnesse.stop();
	}

	private FitNesseContext loadContext() throws Exception {
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
		context.htmlPageFactory = componentFactory.getHtmlPageFactory(new HtmlPageFactory());
		context.testResultsDirectoryName = fitNesseCommanderConfig.getTestResultsDirectoryName();

		context.root = wikiPageFactory.makeRootPage(context.rootPath, context.rootDirectoryName, componentFactory);

		WikiImportTestEventListener.register();

		return context;
	}

	public void runTest(final String testName) throws MojoExecutionException {
		callUrl(getTestUrl(testName, PageType.TEST, null));
	}

	public void runTestSuite(final String suiteName) throws MojoExecutionException {
		callUrl(getTestUrl(suiteName, PageType.SUITE, null));
	}

	public void runByTagFilter(final String suiteFilter, final String suitePageName) throws MojoExecutionException {
		callUrl(getTestUrl(suitePageName, PageType.SUITE, suiteFilter));
	}

	private void callUrl(final String testUrl) throws MojoExecutionException {
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			final URL url = new URL("http", addr.getHostAddress(), fitNesseCommanderConfig.getFitNessePort(), testUrl);
			final URLConnection yc = url.openConnection();
			final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null)
				fitNesseCommanderConfig.getLog().info(inputLine);
			in.close();
		} catch (final UnknownHostException e) {
			throw new MojoExecutionException("Could not make url call", e);
		} catch (final MalformedURLException e) {
			throw new MojoExecutionException("Could not make url call", e);
		} catch (final IOException e) {
			throw new MojoExecutionException("Could not make url call", e);
		}
	}

	private String getTestUrl(final String pageName, final PageType pageType, final String suiteFilter) {
		if (suiteFilter != null)
			return "/" + pageName + "?responder=suite&suiteFilter=" + suiteFilter;
		else
			return "/" + pageName + "?" + pageType.name().toString().toLowerCase();
	}

	public void clearTestResultsDirectory() throws MojoExecutionException {
		final String directoryName = fitNesseCommanderConfig.getRootPath() + File.separatorChar
				+ fitNesseCommanderConfig.getNameRootPage() + File.separatorChar + "files" + File.separatorChar
				+ fitNesseCommanderConfig.getTestResultsDirectoryName();
		try {
			FileUtils.deleteDirectory(directoryName);
		} catch (final IOException e) {
			throw new MojoExecutionException("Could not delete directory: " + directoryName, e);
		}
	}

}
