package nl.sijpesteijn.testing.fitnesse.plugins.managers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import nl.sijpesteijn.testing.fitnesse.plugins.executioners.TestSummaryAndDuration;
import nl.sijpesteijn.testing.fitnesse.plugins.pluginconfigs.ReporterPluginConfig;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ReporterPluginManager implements PluginManager {

    private final ReporterPluginConfig pluginConfig;

    public ReporterPluginManager(final ReporterPluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
    }

    @Override
    public void run() throws MojoFailureException, MojoExecutionException {
        try {
            createIndexFile();
            addFitNesseReports(pluginConfig.getOutputDirectory(), pluginConfig.getFitnesseOutputDirectory());
        } catch (final IOException e) {
            throw new MojoFailureException("Error copying fitnesse reports.", e);
        }
    }

    private void addFitNesseReports(final File reportDirectory, final File destination) throws IOException {
        if (reportDirectory.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdir();
            }

            final String[] children = reportDirectory.list();
            for (int i = 0; i < children.length; i++) {
                addFitNesseReports(new File(reportDirectory, children[i]), new File(destination, children[i]));
            }
        } else {

            final InputStream in = new FileInputStream(reportDirectory);
            final OutputStream out = new FileOutputStream(destination);

            // Copy the bits from instream to outstream
            final byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }

    private void createIndexFile() throws IOException, MojoFailureException {
        final File index = new File(pluginConfig.getOutputDirectory().getAbsolutePath() + "/index.html");
        final List<File> reports = dirListByAscendingName(getReportsFormDirectory(pluginConfig.getOutputDirectory()));
        final TestSummaryAndDuration testSummary = getTestSummaryAndDuration();
        final FileWriter writer = new FileWriter(index);

        final BufferedReader reader = getReportTemplate();
        String line;
        while ((line = reader.readLine()) != null) {
            line = resolvePlaceHolders(line, testSummary, reports);
            writer.write(line);
        }
        reader.close();
        writer.close();
    }

    private BufferedReader getReportTemplate() throws FileNotFoundException {
        if (pluginConfig.getReportTemplate() == null || pluginConfig.getReportTemplate().equals("")) {
            final InputStream inputStream = this.getClass().getResourceAsStream("report.html");
            final InputStreamReader reader = new InputStreamReader(inputStream);

            return new BufferedReader(reader);
        }
        return new BufferedReader(new FileReader(pluginConfig.getReportTemplate()));
    }

    private String resolvePlaceHolders(final String line, final TestSummaryAndDuration testSummary,
                                       final List<File> reports)
    {
        if (line.contains("${success}")) {
            line.replace("${success}", "" + testSummary.getRight());
        }
        if (line.contains("${failure}")) {
            line.replace("${failure}", "" + testSummary.getWrong());
        }
        if (line.contains("${exception}")) {
            line.replace("${exception}", "" + testSummary.getExceptions());
        }
        if (line.contains("${ignore}")) {
            line.replace("${ignore}", "" + testSummary.getIgnores());
        }
        if (line.contains("${time}")) {
            line.replace("${time}", "" + testSummary.getDuration());
        }
        if (line.contains("${time}")) {
            String reportLine = "<ul>";
            for (final File report : reports) {
                if (isNotIndexFile(report))
                    reportLine +=
                            "\t\t\t<li><a href=\"" + report.getAbsolutePath() + "\">" + report.getName()
                                    + "</a></li>\n";
            }
            reportLine += "</ul>";
            line.replace("${reports}", reportLine);
        }

        return line;
    }

    private TestSummaryAndDuration getTestSummaryAndDuration() throws MojoFailureException {
        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        final String fileLocation = pluginConfig.getOutputDirectory() + "/summary.xml";
        final TestSummaryAndDuration summary;
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
            final Document doc = docBuilder.parse(new File(fileLocation));
            final String success = getNodeValue(doc, "success");
            final String failure = getNodeValue(doc, "failure");
            final String exception = getNodeValue(doc, "exception");
            final String ignore = getNodeValue(doc, "ignore");
            final String time = getNodeValue(doc, "time");
            summary =
                    new TestSummaryAndDuration(Integer.parseInt(success), Integer.parseInt(failure),
                        Integer.parseInt(ignore), Integer.parseInt(exception), Long.parseLong(time));
        } catch (final Throwable e) {
            throw new MojoFailureException("Could not find " + fileLocation + " file: " + e.getMessage());
        }
        return summary;
    }

    private String getNodeValue(final Document doc, final String node) {
        final NodeList nodeList = doc.getElementsByTagName(node);
        final Element nodeElement = (Element) nodeList.item(0);

        final NodeList textFNList = nodeElement.getChildNodes();
        return (textFNList.item(0)).getNodeValue().trim();
    }

    private boolean isNotIndexFile(final File report) {
        return !report.getName().endsWith("index.html") && !report.getName().equals("");
    }

    private File[] getReportsFormDirectory(final File dir) {
        final List<File> filtered = new ArrayList<File>();
        for (final File file : dir.listFiles()) {
            if (file.getName().endsWith("html")) {
                filtered.add(file);
            }
        }
        final File[] files = new File[filtered.size()];
        return filtered.toArray(files);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List<File> dirListByAscendingName(final File[] files) {
        Arrays.sort(files, new Comparator() {
            @Override
            public int compare(final Object o1, final Object o2) {
                return ((File) o1).getName().compareToIgnoreCase(((File) o2).getName());
            }
        });
        return Arrays.asList(files);
    }
}
