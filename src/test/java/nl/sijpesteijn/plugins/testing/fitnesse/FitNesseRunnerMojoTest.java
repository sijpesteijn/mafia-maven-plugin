package nl.sijpesteijn.plugins.testing.fitnesse;

import nl.sijpesteijn.testing.fitnesse.plugins.FitnesseRunnerMojo;

import org.junit.Ignore;

@Ignore
public class FitNesseRunnerMojoTest extends AbstractFitNesseTestCase {
	private FitnesseRunnerMojo mojo;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mojo = configureFitNesseMojo(new FitnesseRunnerMojo(), "run");
	}
}
