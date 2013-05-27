package nl.sijpesteijn.testing.fitnesse.plugins.runner;

import nl.sijpesteijn.testing.fitnesse.plugins.report.MafiaTestSummary;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaException;

import java.io.*;

/**
 * DiskResultStore.
 */
public class DiskResultStore implements ResultStore {

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public final MafiaTestSummary saveResult(final String content, final File resultsDirectory,
                                       final String fileName) throws MafiaException {
        resultsDirectory.mkdirs();
        File resultFile = new File(resultsDirectory, fileName + ".html");
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
                writer.write(line);
            }
            writer.close();
            fos.close();
        } catch (IOException e) {
            throw new MafiaException("Could not save test result.", e);
        }
        return summary;
    }

    /**
     * Update the paths.
     *
     * @param line - html string.
     * @return - updated html.
     */
    private String updatePaths(final String line) {
        if (line.contains("/files/fitnesse/")) {
            return line.replaceAll("/files/fitnesse/", "./");
        }
        return line;
    }

    /**
     * Update the mafia test summary with result.
     *
     * @param inputLine - html result string.
     * @return - mafia test summary.
     */
    private MafiaTestSummary updateSummary(final String inputLine) {
        final MafiaTestSummary summary = new MafiaTestSummary();
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
        return summary;
    }

}
