package com.vsa.ecommerce.common.abstraction;

import com.vsa.ecommerce.common.exception.BusinessStatus;

/**
 * Custom runtime exception used to signal business logic failures.
 * Carries a BusinessCode to provide context about the error.
 */
public class BusinessException extends RuntimeException {
    private final BusinessStatus status;

    public BusinessException(BusinessStatus status) {
        super(status.getMessage());
        this.status = status;
    }

    public BusinessException(BusinessStatus status, Object... args) {
        super(String.format(status.getMessage(), args));
        this.status = status;
    }

    public BusinessStatus getStatus() {
        return status;
    }
}
