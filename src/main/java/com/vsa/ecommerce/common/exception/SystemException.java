package com.vsa.ecommerce.common.exception;

/**
 * Custom runtime exception used to signal system-level failures.
 * Carries a SystemStatus to provide context about the error.
 */
public class SystemException extends RuntimeException {
    public SystemException(String message) {
        super(message);
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }
}
