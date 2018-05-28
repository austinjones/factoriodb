package org.factoriodb.graph;

/**
 * @author austinjones
 */
public class StablizationException extends RuntimeException {
    public StablizationException() {
        super();
    }

    public StablizationException(String message) {
        super(message);
    }

    public StablizationException(String message, Throwable cause) {
        super(message, cause);
    }

    public StablizationException(Throwable cause) {
        super(cause);
    }

    protected StablizationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
