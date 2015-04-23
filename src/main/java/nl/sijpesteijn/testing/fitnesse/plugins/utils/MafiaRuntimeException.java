package nl.sijpesteijn.testing.fitnesse.plugins.utils;

/**
 * MafiaException.
 */
@SuppressWarnings("serial")
public class MafiaRuntimeException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message
     *            - the error message.
     * @param throwable
     *            - the exception
     */
    public MafiaRuntimeException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Constructor.
     *
     * @param message
     *            - the error message.
     */
    public MafiaRuntimeException(final String message) {
        super(message);
    }

    public MafiaRuntimeException(Throwable e) {
        super(e);
    }
}
