package nl.sijpesteijn.testing.fitnesse.plugins.runner;

import fitnesse.wiki.PageType;
import nl.sijpesteijn.testing.fitnesse.plugins.report.MafiaTestSummary;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaException;

import java.io.*;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import com.ximpleware.AutoPilot;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathParseException;


/**
 * DiskResultStore.
 */
public class DiskResultStore implements ResultStore {

    private static final String RESULTS_FILE = "fitnesse-results.xml";

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public final MafiaTestSummary saveResult(final String content, final File resultsDirectory, final Long testTime,
                                             PageType pageType, String wikiPage, final String suiteFilter) throws MafiaException {
        resultsDirectory.mkdirs();
        MafiaTestSummary summary = null;
        File resultFile = new File(resultsDirectory, RESULTS_FILE);
        
        try {
            FileUtils.write(resultFile, content);
            summary = updateSummary(resultFile);
            summary.setWikiPage(wikiPage);
            summary.setSuiteFilter(suiteFilter);
            summary.setTestTime(testTime);
            summary.setPageType(pageType);
            saveSummary(summary, resultsDirectory);
        } catch (IOException e) {
            throw new MafiaException("Could not save test result.", e);
        } catch (XPathParseException e) {
            throw new MafiaException("Could not parse test result.", e);
        }
        return summary;
    }

    @Override
    public void saveSummary(MafiaTestSummary testSummary, File resultsDirectory) throws MafiaException {
        FileOutputStream outputStream;
        final Properties properties = new Properties();
        properties.put("exceptions", String.valueOf(testSummary.getExceptions()));
        properties.put("wrong", String.valueOf(testSummary.getWrong()));
        properties.put("ignores", String.valueOf(testSummary.getIgnores()));
        properties.put("right",  String.valueOf(testSummary.getRight()));
        properties.put("testTime", String.valueOf(testSummary.getTestTime()));
        properties.put("runDate", String.valueOf(new Date().getTime()));
        properties.put("pageType", String.valueOf(testSummary.getPageType()));
        properties.put("wikiPage", String.valueOf(testSummary.getWikiPage()));
        if (testSummary.getSuiteFilter() != null)
            properties.put("suiteFilter", String.valueOf(testSummary.getSuiteFilter()));

        try {
            outputStream = new FileOutputStream(resultsDirectory + File.separator + "mafiaresults.properties");
        } catch (FileNotFoundException e) {
            throw new MafiaException("Could not open mafiaresults.properties", e);
        }

        try {
            properties.store(outputStream, "Mafia test result properties");
            outputStream.flush();
        } catch (IOException e) {
            throw new MafiaException("Could not save mafia test results.", e);
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                throw new MafiaException("Could not close property file stream.", e);
            }
        }
    }

    @Override
    public MafiaTestSummary getSummary(File resultsDirectory) throws MafiaException {
        final Properties properties = new Properties();
        MafiaTestSummary summary = null;
        if (resultsDirectory.exists()) {
            try (final InputStream is = new FileInputStream(resultsDirectory)) {
                try {
                    properties.load(is);
                    int wrong = Integer.parseInt(properties.getProperty("wrong"));
                    int right = Integer.parseInt(properties.getProperty("right"));
                    int ignores = Integer.parseInt(properties.getProperty("ignores"));
                    int exceptions = Integer.parseInt(properties.getProperty("exceptions"));
                    summary  = new MafiaTestSummary(right, wrong, ignores, exceptions);
                    summary.setTestTime(Long.parseLong(properties.getProperty("testTime")));
                    summary.setRunDate(Long.parseLong(properties.getProperty("runDate")));
                    if(properties.containsKey("suiteFilter"))
                        summary.setSuiteFilter(String.valueOf(properties.getProperty("suiteFilter")));
                    summary.setWikiPage(String.valueOf(properties.getProperty("wikiPage")));
                    summary.setPageType(PageType.fromString(String.valueOf(properties.getProperty("pageType"))));
                } finally {
                    is.close();
                }
            } catch (IOException e) {
                throw new MafiaException("Failed to open mafia test result directory.", e);
            }
        }
        return summary;
    }


    /**
     * Update the mafia test summary with result.
     *
     * @param resultFile
     *            - fitnesse result file.
     * @return - mafia test summary.
     * @throws XPathParseException 
     */
    private MafiaTestSummary updateSummary(final File resultFile) throws XPathParseException {
        VTDGen parser = new VTDGen();
        parser.parseFile(resultFile.getAbsolutePath(), true);
        VTDNav vn = parser.getNav();
        AutoPilot ap = new AutoPilot(vn);
        ap.selectXPath("//finalCounts/right");
        int right  = Integer.valueOf(ap.evalXPathToString());
        ap.selectXPath("//finalCounts/wrong");
        int wrong  = Integer.valueOf(ap.evalXPathToString());
        ap.selectXPath("//finalCounts/ignores");
        int ignores  = Integer.valueOf(ap.evalXPathToString());
        ap.selectXPath("//finalCounts/exceptions");
        int exceptions  = Integer.valueOf(ap.evalXPathToString());
        final MafiaTestSummary summary = new MafiaTestSummary(right, wrong, ignores, exceptions);
        return summary;
    }

}
