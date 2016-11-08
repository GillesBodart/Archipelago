package org.archipelago.core.exception;

/**
 * Created by Gilles Bodart on 18/08/2016.
 */
public class CheckException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 6666496705775508281L;

    public CheckException() {
        super();
    }

    public CheckException(final String message) {
        super(message);
    }

    public CheckException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CheckException(final Throwable cause) {
        super(cause);
    }

    protected CheckException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
