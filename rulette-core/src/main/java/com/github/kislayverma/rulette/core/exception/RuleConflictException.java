package com.github.kislayverma.rulette.core.exception;

/**
 * This exception is thrown when conflicting rules are detected in the system. This may happen when a rule system is
 * being loaded, or when a rule is being added/modified.
 */
public class RuleConflictException extends Exception {
    public RuleConflictException(String message) {
        super(message);
    }

    public RuleConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
