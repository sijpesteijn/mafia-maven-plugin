package nl.sijpesteijn.testing.fitnesse.plugins.utils;

/**
 * MafiaException.
 */
public class MafiaException extends Exception {

    /**
     * Constructor.
     *
     * @param message   - the error message.
     * @param throwable - the exception
     */
    public MafiaException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Constructor.
     *
     * @param message - the error message.
     */
    public MafiaException(final String message) {
        super(message);
    }
}
