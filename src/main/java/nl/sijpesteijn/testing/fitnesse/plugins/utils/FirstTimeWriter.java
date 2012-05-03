package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * When you first start using this plugin, we will create a default setup.
 * 
 */
public class FirstTimeWriter {

    private final String fitnesseLocation;

    /**
     * 
     * @param fitnesseLocation
     *            {@link java.lang.String}
     * @throws IOException
     */
    public FirstTimeWriter(final String fitnesseLocation) throws IOException {
        this.fitnesseLocation = fitnesseLocation;
        updateFrontPageContent();
        createSpecialPages();
    }

    /**
     * Create a new FrontPage wiki page.
     * 
     * @throws IOException
     */
    private void updateFrontPageContent() throws IOException {
        final File frontPageContent = new File(fitnesseLocation + "/FrontPage/content.txt");
        final FileWriter frontPageWriter = new FileWriter(frontPageContent);
        frontPageWriter.write("!contents");
        frontPageWriter.close();
    }

    /**
     * Add the setup, teardown, suitesetup and suiteteardown wiki pages.
     * 
     * @throws IOException
     */
    private void createSpecialPages() throws IOException {
        createWikiPage("SetUp", SpecialPages.SetUpContentTxt, SpecialPages.PropertiesXml);
        createWikiPage("TearDown", SpecialPages.TearDownContentTxt, SpecialPages.PropertiesXml);
        createWikiPage("SuiteSetUp", SpecialPages.SuiteSetUpContentTxt, SpecialPages.PropertiesXml);
        createWikiPage("SuiteTearDown", SpecialPages.SuiteTearDownContentTxt, SpecialPages.PropertiesXml);
    }

    /**
     * Create the wiki page and folder for FitNesse.
     * 
     * @param pageName
     *            {@link java.lang.String}
     * @param contentTxt
     *            {@link java.lang.String}
     * @param propertiesXml
     *            {@link java.lang.String}
     * @throws IOException
     */
    private void createWikiPage(final String pageName, final String contentTxt, final String propertiesXml)
            throws IOException {
        final File setupDir = new File(fitnesseLocation + "/FrontPage/" + pageName + "/");
        setupDir.mkdirs();
        final File contentTxtFile = new File(fitnesseLocation + "/FrontPage/" + pageName + "/" + "content.txt");
        final FileWriter contentFileWriter = new FileWriter(contentTxtFile);
        contentFileWriter.write(contentTxt);
        contentFileWriter.close();
        final File propertiesXmlFile = new File(fitnesseLocation + "/FrontPage/" + pageName + "/" + "properties.xml");
        final FileWriter propertiesFileWriter = new FileWriter(propertiesXmlFile);
        propertiesFileWriter.write(propertiesXml);
        propertiesFileWriter.close();
    }

}
