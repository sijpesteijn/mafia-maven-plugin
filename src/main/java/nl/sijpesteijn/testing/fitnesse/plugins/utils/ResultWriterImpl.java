package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import nl.sijpesteijn.testing.fitnesse.plugins.executioners.TestSummaryAndDuration;

import org.apache.maven.plugin.MojoExecutionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ResultWriterImpl implements ResultWriter {

    private final String outputDirectory;

    public ResultWriterImpl(final String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void write(final TestSummaryAndDuration summary) throws MojoExecutionException {
        final String outputFile = "summary.xml";
        final File outputDir = new File(outputDirectory);
        outputDir.mkdirs();
        final File summaryXML = new File(outputDirectory, outputFile);
        try {
            final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            final Document doc = docBuilder.newDocument();
            final Element rootElement = doc.createElement("fitnesse");
            doc.appendChild(rootElement);

            final Element resultElement = doc.createElement("result");
            rootElement.appendChild(resultElement);

            final Element successElement = doc.createElement("success");
            successElement.appendChild(doc.createTextNode("" + summary.right));
            resultElement.appendChild(successElement);

            final Element failureElement = doc.createElement("failure");
            failureElement.appendChild(doc.createTextNode("" + summary.wrong));
            resultElement.appendChild(failureElement);

            final Element exceptionElement = doc.createElement("exception");
            exceptionElement.appendChild(doc.createTextNode("" + summary.exceptions));
            resultElement.appendChild(exceptionElement);

            final Element ignoreElement = doc.createElement("ignore");
            ignoreElement.appendChild(doc.createTextNode("" + summary.ignores));
            resultElement.appendChild(ignoreElement);

            final Element timeElement = doc.createElement("time");
            timeElement.appendChild(doc.createTextNode("" + summary.duration));
            resultElement.appendChild(timeElement);

            final DOMSource source = new DOMSource(doc);

            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Transformer transformer = transformerFactory.newTransformer();

            final StreamResult result = new StreamResult(summaryXML);

            transformer.transform(source, result);
        } catch (final ParserConfigurationException pce) {
            throw new MojoExecutionException("Could not create " + outputFile, pce);
        } catch (final TransformerException te) {
            throw new MojoExecutionException("Could not create " + outputFile, te);
        }
    }

}
