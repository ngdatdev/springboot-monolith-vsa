package com.vsa.monolith.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final Integer errorCode;
    private final String errorMessage;

    public BusinessException(String errorMessage) {
        super(errorMessage);
        this.errorCode = 400;
        this.errorMessage = errorMessage;
    }

    public BusinessException(Integer errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
