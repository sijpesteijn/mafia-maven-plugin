package nl.sijpesteijn.testing.fitnesse.plugins.utils;

/**
 * Utility class used by
 * {@link nl.sijpesteijn.testing.fitnesse.plugins.utils.FirstTimeWriter}
 * 
 */
public class SpecialPages {

    public static final String PropertiesXml = "<?xml version=\"1.0\"?>\n" + "<properties>\n" + "<Edit>true</Edit>\n"
            + "<Files>true</Files>\n" + "<Properties>true</Properties>\n" + "<RecentChanges>true</RecentChanges>\n"
            + "<Refactor>true</Refactor>\n" + "<Search>true</Search>\n" + "<Versions>true</Versions>\n"
            + "<WhereUsed>true</WhereUsed>\n" + "</properties>";

    public static final String SetUpContentTxt = "|import|\n" + "|${fixturePackage}|";

    public static final String TearDownContentTxt = "";

    public static final String SuiteSetUpContentTxt = "|import|\n" + "|${fixturePackage}|";

    public static final String SuiteTearDownContentTxt = "";

}
