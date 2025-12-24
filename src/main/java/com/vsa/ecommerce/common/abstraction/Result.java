package com.vsa.ecommerce.common.abstraction;

import java.util.Date;
import java.util.List;
import lombok.Getter;

/**
 * Unified response wrapper for API results.
 * Contains data, success status, and error details.
 * Used by ResponseAdvice to standardize API responses.
 */
public class Result<T> {
    private final T data;
    private final boolean isSuccess;
    private final List<String> errors;
    private final Date timestamp = new Date();
    
    public T getData() { return data; }
    public boolean isSuccess() { return isSuccess; }
    public List<String> getErrors() { return errors; }
    public Date getTimestamp() { return timestamp; }

    // Private constructor, use static factories
    private Result(T data, boolean isSuccess, List<String> errors) {
        this.data = data;
        this.isSuccess = isSuccess;
        this.errors = errors;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(data, true, null);
    }

    public static <T> Result<T> failure(List<String> errors) {
        return new Result<>(null, false, errors);
    }
}
