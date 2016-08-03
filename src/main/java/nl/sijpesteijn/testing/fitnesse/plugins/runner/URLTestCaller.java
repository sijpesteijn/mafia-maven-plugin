package nl.sijpesteijn.testing.fitnesse.plugins.runner;

import fitnesse.wiki.PageType;
import nl.sijpesteijn.testing.fitnesse.plugins.report.MafiaTestSummary;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaException;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * URL Test caller.
 */
public class URLTestCaller implements TestCaller {

    /**
     * FitNesse port.
     */
    private final int fitNessePort;

    /**
     * Protocol.
     */
    private final String protocol;

    /**
     * Host.
     */
    private final String host;

    /**
     * Test results directory.
     */
    private File testResultsDirectory;

    /**
     * Result store.
     */
    private final ResultStore resultStore;

    /**
     * Constructor.
     *
     * @param fitNessePort
     *            - fitnesse port.
     * @param protocol
     *            - portocol.
     * @param host
     *            - host.
     * @param testResultsDirectory
     *            - test results directory.
     * @param resultStore
     *            - result store.
     */
    public URLTestCaller(final int fitNessePort, final String protocol, final String host,
        final File testResultsDirectory, final ResultStore resultStore) {

        this.fitNessePort = fitNessePort;
        this.protocol = protocol;
        this.host = host;
        this.testResultsDirectory = testResultsDirectory;
        this.resultStore = resultStore;
    }

    /**
     *
     * {@inheritDoc}
     *
     */
    @Override
    public final MafiaTestSummary test(final String wikiPage, final PageType pageType, final String suiteFilter,
        final String subDirectory) throws MafiaException {
        String testUrl = getTestUrl(wikiPage, pageType, suiteFilter);

        MafiaTestSummary summary = null;
        URL url = null;
        try {
            url = new URL(protocol, host, fitNessePort, testUrl);
            long start = System.currentTimeMillis();
            String content = IOUtils.toString(url, Charset.defaultCharset());
            long testTime = System.currentTimeMillis() - start;
            File resultsDirectory = new File(new File(testResultsDirectory, subDirectory), wikiPage);

            summary = resultStore.saveResult(content, resultsDirectory, testTime, pageType, wikiPage, suiteFilter);
        } catch (MalformedURLException e) {
            throw new MafiaException("Could not make url call.", e);
        } catch (IOException e) {
            throw new MafiaException("Could not open connection to URL " + url, e);
        }
        return summary;
    }

    /**
     * Get test url.
     *
     * @param pageName
     *            - page name.
     * @param pageType
     *            - page type.
     * @param suiteFilter
     *            - suite filter.
     * @return - url string.
     */
    private String getTestUrl(final String pageName, final PageType pageType, final String suiteFilter) {
        if (suiteFilter != null) {
            return "/" + pageName + "?responder=suite&format=xml&suiteFilter=" + suiteFilter;
        } else {
            return "/" + pageName + "?" + pageType.name().toString().toLowerCase()+"&format=xml";
        }
    }

}
