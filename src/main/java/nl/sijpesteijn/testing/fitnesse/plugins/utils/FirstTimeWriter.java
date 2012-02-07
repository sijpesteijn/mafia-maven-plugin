package nl.sijpesteijn.testing.fitnesse.plugins.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FirstTimeWriter {

    private final String nameRootPage;

    public FirstTimeWriter(final String nameRootPage) throws IOException {
        this.nameRootPage = nameRootPage;
        updateFrontPageContent();
        createSpecialPages();
    }

    private void updateFrontPageContent() throws IOException {
        final File frontPageContent = new File(nameRootPage + "/FrontPage/content.txt");
        final FileWriter frontPageWriter = new FileWriter(frontPageContent);
        frontPageWriter.write("!contents");
        frontPageWriter.close();
    }

    private void createSpecialPages() throws IOException {
        createWikiPage("SetUp", SpecialPages.SetUpContentTxt, SpecialPages.PropertiesXml);
        createWikiPage("TearDown", SpecialPages.TearDownContentTxt, SpecialPages.PropertiesXml);
        createWikiPage("SuiteSetUp", SpecialPages.SuiteSetUpContentTxt, SpecialPages.PropertiesXml);
        createWikiPage("SuiteTearDown", SpecialPages.SuiteTearDownContentTxt, SpecialPages.PropertiesXml);
    }

    private void createWikiPage(final String pageName, final String contentTxt, final String propertiesXml)
            throws IOException
    {
        final File setupDir = new File(nameRootPage + "/FrontPage/" + pageName + "/");
        setupDir.mkdirs();
        final File contentTxtFile = new File(nameRootPage + "/FrontPage/" + pageName + "/" + "content.txt");
        final FileWriter contentFileWriter = new FileWriter(contentTxtFile);
        contentFileWriter.write(contentTxt);
        contentFileWriter.close();
        final File propertiesXmlFile = new File(nameRootPage + "/FrontPage/" + pageName + "/" + "properties.xml");
        final FileWriter propertiesFileWriter = new FileWriter(propertiesXmlFile);
        propertiesFileWriter.write(propertiesXml);
        propertiesFileWriter.close();
    }

}
