package nl.sijpesteijn.plugins.testing.fitnesse;

import java.io.File;

import nl.sijpesteijn.testing.fitnesse.plugins.FitnesseContentMojo;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class FitnesseContentMojoTest extends AbstractMojoTestCase {

    @Test
    public void test() throws Exception {
        final File testPom = new File(getBasedir(), "src/test/resources/mafia-plugin-content.xml");
        final FitnesseContentMojo mojo = (FitnesseContentMojo) lookupMojo("content", testPom);
        assertNotNull(mojo);
    }

}
