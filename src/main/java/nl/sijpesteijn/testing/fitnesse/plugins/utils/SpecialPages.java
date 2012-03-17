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

    public static final String javascript = "var collapsableOpenCss = \"collapsable\";var collapsableClosedCss = \"hidden\";var collapsableOpenImg = \"./images/collapsableOpen.gif\";var collapsableClosedImg = \"./images/collapsableClosed.gif\";function toggleCollapsable(id){  var div = document.getElementById(id);  var img = document.getElementById(\"img\" + id);  if (div.className.indexOf(collapsableClosedCss) != -1)  {    div.className = collapsableOpenCss;    img.src = collapsableOpenImg;  }  else  {    div.className = collapsableClosedCss;    img.src = collapsableClosedImg;  }}function expandOrCollapseAll(cssClass){  divs = document.getElementsByTagName(\"div\");  for (i = 0; i < divs.length; i++)  {    div = divs[i];    if (div.className == cssClass)    {      toggleCollapsable(div.id);    }  }}function collapseAll(){  expandOrCollapseAll(collapsableOpenCss);}function expandAll(){  expandOrCollapseAll(collapsableClosedCss);}";
}
