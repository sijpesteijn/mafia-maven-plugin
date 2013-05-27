package nl.sijpesteijn.testing.fitnesse.plugins.utils;


import org.junit.Test;

/**
 * User: gijs
 * Date: 5/19/13 11:47 AM
 */
public class MafiaExceptionTest {

    @Test(expected = MafiaException.class)
    public void testException() throws Throwable {
        throw new MafiaException("Moe");
    }
}
