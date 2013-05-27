package nl.sijpesteijn.testing.fitnesse.plugins.report;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

/**
 * User: gijs
 * Date: 5/20/13 2:41 PM
 */
public class MafiaTestResultRepositoryTest {
    private MafiaTestResultRepository mafiaTestResultRepository;

    @Before
    public void setup() throws Throwable {
        mafiaTestResultRepository = new MafiaTestResultRepository(new File("target/test-classes/sampleResults/"));
    }

    @Test
    public void testSuiteResults() throws Exception {
        assertTrue(mafiaTestResultRepository.getSuitesResults().size() == 2);
    }

    @Test
    public void testTestResults() throws Exception {
        assertTrue(mafiaTestResultRepository.getTestResults().size() == 1);
    }

    @Test
    public void testFilteredSuiteResults() throws Exception {
        assertTrue(mafiaTestResultRepository.getFilteredSuitesResults().size() == 0);
    }

    @Test
    public void testDirectoryFailure() throws Throwable {
        mafiaTestResultRepository = new MafiaTestResultRepository(new File("target/test-classes/IDONTEXIST/"));
        mafiaTestResultRepository.getTestResults();
    }
}
