package nl.sijpesteijn.testing.fitnesse.plugins.report;

import nl.sijpesteijn.testing.fitnesse.plugins.runner.DiskResultStore;
import nl.sijpesteijn.testing.fitnesse.plugins.runner.ResultStore;
import nl.sijpesteijn.testing.fitnesse.plugins.utils.MafiaException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: gijs
 * Date: 5/20/13 2:41 PM
 */
public class MafiaTestResultRepositoryTest {
    private ResultStore resultStoreMock = new DiskResultStore();
    private MafiaTestResultRepository mafiaTestResultRepository;

    @Before
    public void setup() throws Throwable {
        mafiaTestResultRepository = new MafiaTestResultRepository(new File("./src/test/resources/sampleResults/"), resultStoreMock);
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
    public void testDirectoryFailure() throws Exception {
        try {
            mafiaTestResultRepository = new MafiaTestResultRepository(new File("target/test-classes/IDONTEXIST/"),resultStoreMock);
            mafiaTestResultRepository.getTestResults();
        } catch(MafiaException me) {
            assertEquals(me.getMessage(), "Could not make mafia test result.");
        }
    }
}
