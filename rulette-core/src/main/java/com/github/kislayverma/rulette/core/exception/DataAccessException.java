package com.github.kislayverma.rulette.core.exception;

/**
 * This exception is thrown when any error happens at the {@link com.github.kislayverma.rulette.core.data.IDataProvider}
 * layer
 */
public class DataAccessException extends RuntimeException {
    public DataAccessException() {
        super();
    }

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
