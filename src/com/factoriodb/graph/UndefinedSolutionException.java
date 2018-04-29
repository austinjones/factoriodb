package com.factoriodb.graph;

/**
 * @author austinjones
 */
public class UndefinedSolutionException extends RuntimeException {
    public UndefinedSolutionException() {
    }

    public UndefinedSolutionException(String message) {
        super(message);
    }

    public UndefinedSolutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public UndefinedSolutionException(Throwable cause) {
        super(cause);
    }

    public UndefinedSolutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
