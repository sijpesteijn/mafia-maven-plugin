package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.BasePluginConfig;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

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

	private final BasePluginConfig config;
	private FitNesse fitnesse;
	private final TestSummary summary = new TestSummary();
	private Process process;
	private InputStreamToBufferMonitor errorMonitor;
	private InputStreamToBufferMonitor inputMonitor;
	private int exitValue;
	final DependencyResolver resolver;

	public FitNesseCommander(final BasePluginConfig config) throws MojoFailureException {
		this.config = config;
		resolver = new DependencyResolver(config.getRepositoryDirectory());
	}

	public void start(final String command) throws MojoExecutionException {
		run(command);
	}

	public void stop(final String command) throws MojoExecutionException {
		run(command);
	}

	public void run(final String command) throws MojoExecutionException {
		try {
			process = Runtime.getRuntime().exec(command, null, new File(config.getWikiRoot()));
			waitForSetupToFinish();
		} catch (final IOException e) {
			throw new MojoExecutionException(e.getMessage());
		} catch (final InterruptedException e) {
			throw new MojoExecutionException(e.getMessage());
		}
	}

	/**
	 * Check if the unpacking of FitNesse has finished.
	 * 
	 * @param endCondition
	 * 
	 * @return {@link boolean}
	 * @throws InterruptedException
	 */
	private void waitForSetupToFinish() throws InterruptedException {
		createStreamMonitors(process);
		while (true) {
			try {
				exitValue = process.exitValue();
				return;
			} catch (final IllegalThreadStateException itse) {
				// Process has not finished yet
			}
			if (inputMonitor.isFinished()) {
				return;
			}
			Thread.sleep(2000);
		}
	}

	/**
	 * Create the stream monitors.
	 * 
	 * @param process
	 *            {@link java.lang.Process}
	 */
	private void createStreamMonitors(final Process process) {
		final InputStream errorStream = process.getErrorStream();
		errorMonitor = new InputStreamToBufferMonitor(errorStream, new StringBuilder());
		new Thread(errorMonitor).start();
		final InputStream inputStream = process.getInputStream();
		inputMonitor = new InputStreamToBufferMonitor(inputStream, new StringBuilder());
		new Thread(inputMonitor).start();
	}

	/**
	 * Check if the buffer contains the specified string.
	 * 
	 * @param buffer
	 *            {@link java.lang.StringBuilder}
	 * @param string
	 *            {@link java.lang.String}
	 * @return {@link boolean}
	 */
	private boolean bufferContains(final StringBuilder buffer, final String string) {
		return buffer.toString().indexOf(string) > 0;
	}

	/**
	 * Is the error buffer empty?
	 * 
	 * @return {@link boolean}
	 */
	public boolean errorBufferHasContent() {
		return errorMonitor.getBuffer().length() > 0;
	}

	/**
	 * Check if the error buffer contains the specified string.
	 * 
	 * @param string
	 *            {@link java.lang.String}
	 * @return {@link boolean}
	 */
	public boolean errorBufferContains(final String string) {
		return bufferContains(errorMonitor.getBuffer(), string);
	}

	/**
	 * Get the error buffer contents.
	 * 
	 * @return {@link java.lang.CharSequence}
	 */
	public CharSequence getErrorBuffer() {
		return errorMonitor.getBuffer();
	}

	public int getExitValue() {
		return exitValue;
	}

	public void start() throws MojoFailureException {
		final FitNesseContext context = loadContext();
		VelocityFactory.makeVelocityFactory(context);
		PageVersionPruner.daysTillVersionsExpire = config.getRetainDays();
		fitnesse = new FitNesse(context);

		fitnesse.start();
		if (!fitnesse.isRunning()) {
			throw new MojoFailureException("Could not start fitnesse.");
		}
	}

	public void stop() throws MojoFailureException {
		final FitNesseContext context = loadContext();
		VelocityFactory.makeVelocityFactory(context);
		PageVersionPruner.daysTillVersionsExpire = config.getRetainDays();
		fitnesse = new FitNesse(context);

		try {
			fitnesse.stop();
		} catch (final Exception e) {
			throw new MojoFailureException("Could not stop fitnesse.", e);
		}
	}

	public void update() throws MojoExecutionException {
		final Dependency dependency = new Dependency();
		dependency.setArtifactId("fitnesse");
		dependency.setGroupId("org.fitnesse");

		String jarLocation;
		jarLocation = resolver.getJarLocation(config.getDependencies(), dependency);
		final String command = "java" + " -jar " + FileUtils.formatPath(jarLocation) + " -i";
		run(command);
		try {
			final File controlFile = new File(config.getWikiRoot() + "/FitNesseRoot/PageHeader/properties.xml");
			while (!controlFile.exists()) {
				Thread.sleep(1000);
			}
			// Thread.sleep(30000);
		} catch (final InterruptedException e) {
		}

	}

	private FitNesseContext loadContext() throws MojoFailureException {
		final FitNesseContext context = new FitNesseContext();
		context.port = config.getFitnessePort();
		context.rootPath = config.getWikiRoot();
		final ComponentFactory componentFactory = new ComponentFactory(context.rootPath);
		context.rootDirectoryName = config.getNameRootPage();
		context.setRootPagePath();
		final String defaultNewPageContent = componentFactory.getProperty(ComponentFactory.DEFAULT_NEWPAGE_CONTENT);
		if (defaultNewPageContent != null) {
			context.defaultNewPageContent = defaultNewPageContent;
		}
		final WikiPageFactory wikiPageFactory = new WikiPageFactory();
		context.responderFactory = new ResponderFactory(context.rootPagePath);
		context.logger = getLogger();
		context.authenticator = new PromiscuousAuthenticator();
		try {
			context.htmlPageFactory = componentFactory.getHtmlPageFactory(new HtmlPageFactory());
			context.testResultsDirectoryName = config.getMafiaTestResultsDirectory();
			context.root = wikiPageFactory.makeRootPage(context.rootPath, context.rootDirectoryName, componentFactory);
		} catch (final Exception e) {
			throw new MojoFailureException("Could not create fitnesse context", e);
		}
		WikiImportTestEventListener.register();

		return context;
	}

	private Logger getLogger() {
		final String logDirectory = config.getLogDirectory();
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

	public TestSummary runTest(final String testName, final Log mavenLogger) throws MojoExecutionException {
		callUrl(getTestUrl(testName, PageType.TEST, null), mavenLogger);
		return summary;
	}

	public TestSummary runTestSuite(final String suiteName, final Log mavenLogger) throws MojoExecutionException {
		callUrl(getTestUrl(suiteName, PageType.SUITE, null), mavenLogger);
		return summary;
	}

	public TestSummary runByTagFilter(final String suiteFilter, final String suitePageName, final Log mavenLogger)
			throws MojoExecutionException {
		callUrl(getTestUrl(suitePageName, PageType.SUITE, suiteFilter), mavenLogger);
		return summary;
	}

	private void callUrl(final String testUrl, final Log mavenLogger) throws MojoExecutionException {
		String ipAddress = "";
		URL url = null;
		try {
			ipAddress = getIpAddress();
			url = new URL("http", ipAddress, config.getFitnessePort(), testUrl);
			final URLConnection yc = url.openConnection();
			final BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {

				logProgress(inputLine, mavenLogger);

				if (inputLine.contains("getElementById(\"test-summary\")")
						&& inputLine.contains("<strong>Assertions:</strong>")) {
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

	private void logProgress(final String inputLine, final Log mavenLogger) {

		final Pattern pattern = Pattern
				.compile("^.*test_summaries.*[pass|fail]\\\\\">(.*)</span>.*_link\\\\\">(.*)</a>.*class=\\\\\"\\\\\">\\((.*)\\)</span>.*");
		final Matcher matcher = pattern.matcher(inputLine);
		if (matcher.matches()) {
			mavenLogger.info(matcher.group(1) + ": " + matcher.group(2) + " time: " + matcher.group(3));
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
		if (suiteFilter != null) {
			return "/" + pageName + "?responder=suite&suiteFilter=" + suiteFilter;
		} else {
			return "/" + pageName + "?" + pageType.name().toString().toLowerCase();
		}
	}

	public void clearTestResultsDirectory() throws MojoExecutionException {
		final String directoryName = config.getWikiRoot() + File.separatorChar + config.getNameRootPage()
				+ File.separatorChar + "files" + File.separatorChar + config.getMafiaTestResultsDirectory();
		FileUtils.deleteRecursively(new File(directoryName));
	}

	public TestSummary getTestSummary() {
		return this.summary;
	}
}
