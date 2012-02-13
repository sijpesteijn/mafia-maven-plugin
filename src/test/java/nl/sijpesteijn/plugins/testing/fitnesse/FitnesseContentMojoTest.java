package nl.sijpesteijn.plugins.testing.fitnesse;

import java.io.File;

import nl.sijpesteijn.testing.fitnesse.plugins.FitnesseContentMojo;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Before;
import org.junit.Test;

public class FitnesseContentMojoTest extends AbstractMojoTestCase {

	@Before
	public void setup() throws Exception {
		super.setUp();
	}

	@Test
	public void test() throws Exception {
		FitnesseContentMojo mojo = new FitnesseContentMojo();
		File testPom = new File(getBasedir(), "src/test/resources/mafia-plugin-content.xml");
		this.configureMojo(mojo, "mafia-maven-plugin", testPom);
		// final FitnesseContentMojo mojo = (FitnesseContentMojo)
		// lookupMojo("content", testPom);
		mojo.execute();
	}
}
