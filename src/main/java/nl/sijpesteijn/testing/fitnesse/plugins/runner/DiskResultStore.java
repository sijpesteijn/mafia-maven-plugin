package nl.sijpesteijn.testing.fitnesse.plugins.runner;

import fitnesse.wiki.PageType;
import nl.sijpesteijn.testing.fitnesse.plugins.report.MafiaTestSummary;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.FitNesseResourceAccess;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaException;

import java.io.*;
import java.util.Date;
import java.util.Properties;

import static java.text.MessageFormat.format;

/**
 * DiskResultStore.
 */
public class DiskResultStore implements ResultStore {

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public final MafiaTestSummary saveResult(final String content, final File resultsDirectory, final Long testTime,
                                             PageType pageType, String wikiPage, final String suiteFilter) throws MafiaException {
        resultsDirectory.mkdirs();
        File resultFile = new File(resultsDirectory, "wikipage.html");
        MafiaTestSummary summary = null;
        String[] lines = content.split("\n");
        try {
            FileOutputStream fos = new FileOutputStream(resultFile);
            Writer writer = new OutputStreamWriter(fos, "UTF-8");
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                if (line.contains("getElementById(\"test-summary\")")
                    && line.contains("<strong>Assertions:</strong>")) {
                    summary = updateSummary(line);
                }
                line = updatePaths(line);
                writer.write(line + "\n");
            }
            writer.close();
            fos.close();
            summary.setWikiPage(wikiPage);
            summary.setSuiteFilter(suiteFilter);
            summary.setTestTime(testTime);
            summary.setPageType(pageType);
            saveSummary(summary, resultsDirectory);
        } catch (IOException e) {
            throw new MafiaException("Could not save test result.", e);
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
     * Update the paths.
     *
     * @param line
     *            - html string.
     * @return - updated html.
     */
    private String updatePaths(final String line) {
        if (line.contains("/files/fitnesse/")) {
            return line.replaceAll("/files/fitnesse/",
                format("../../{0}/", FitNesseResourceAccess.RESOURCES_FOLDER_NAME_WITHIN_MAFIARESULTS));
        }
        return line;
    }

    /**
     * Update the mafia test summary with result.
     *
     * @param inputLine
     *            - html result string.
     * @return - mafia test summary.
     */
    private MafiaTestSummary updateSummary(final String inputLine) {
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
        int right = Integer.parseInt(rightStr.trim());
        int wrong = Integer.parseInt(wrongStr.trim());
        int ignores = Integer.parseInt(ignoreStr.trim());
        int exceptions = Integer.parseInt(exceptionsStr.trim());
        final MafiaTestSummary summary = new MafiaTestSummary(right, wrong, ignores, exceptions);
        return summary;
    }

}
