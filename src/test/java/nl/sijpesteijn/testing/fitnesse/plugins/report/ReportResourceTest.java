package nl.sijpesteijn.testing.fitnesse.plugins.report;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * User: gijs
 * Date: 5/20/13 10:52 AM
 */
public class ReportResourceTest {
    private ReportResource reportResource;
    private String root = new File("").getAbsolutePath();

    @Before
    public void setup() throws Exception {
        reportResource = new ReportResource(root + "/target/", "");
    }

    @Test
    public void testCopyFile() throws Exception {
        reportResource.copy("testResources/");
    }

    @Test
    public void testCopyDirectoryFromJar() throws Exception {
        reportResource.copy("fit/");
    }

    @Test
    public void testCopyFileFromJar() throws Exception {
        reportResource = new ReportResource(root + "/target/", "eg/");
        reportResource.copy("employeePayroll/Employees.class");
    }

    @Test
    public void testAddSeparatorFile() throws Exception {
        reportResource = new ReportResource(root + "/target", "");
        reportResource.copy("testResources/");
    }
}
