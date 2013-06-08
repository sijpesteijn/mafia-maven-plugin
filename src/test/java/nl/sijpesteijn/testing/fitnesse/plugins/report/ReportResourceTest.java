package nl.sijpesteijn.testing.fitnesse.plugins.report;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * User: gijs
 * Date: 5/20/13 10:52 AM
 */
public class ReportResourceTest {
    private ReportResource reportResource;
    private String root;
    private char separator = '/';

    @Before
    public void setup() throws Exception {
        root = new File("").getAbsolutePath();
        reportResource = new ReportResource(root + "/target/", "");
    }

    @Test
    public void testCopyFile() throws Exception {
        reportResource.copy("testResources" + separator);
    }

    @Test
    public void testCopyDirectoryFromJar() throws Exception {
        reportResource.copy("fit" + separator);
    }

    @Test
    public void testCopyFileFromJar() throws Exception {
        reportResource = new ReportResource(root + "/target", "eg" + separator);
        reportResource.copy("employeePayroll" + separator + "Employees.class");
        FileUtils.deleteDirectory(new File("./targetemployeePayroll"));
    }

    @Test
    public void testAddSeparatorFile() throws Exception {
        reportResource = new ReportResource(root + "/target", "");
        reportResource.copy("testResources" + separator);
    }
}
