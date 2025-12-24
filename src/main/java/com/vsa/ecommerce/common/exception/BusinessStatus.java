package com.vsa.ecommerce.common.exception;

import lombok.Getter;

/**
 * Defines business-specific result codes and messages.
 * Used for detailed error reporting beyond standard HTTP status codes.
 */
@Getter
public enum BusinessStatus {
    SUCCESS("Success"),
    FAILURE("Failure"),
    UNAUTHORIZED("Unauthorized"),
    NOT_FOUND("Not Found"),
    INTERNAL_SERVER_ERROR("Internal Server Error"),
    PARAM_ERROR("Parameter Error"),
    BUSINESS_ERROR("Business Logic Error"),

    // --- Standard HTTP Errors ---
    BAD_REQUEST("Bad Request"),
    FORBIDDEN("Forbidden"),
    METHOD_NOT_ALLOWED("Method Not Allowed"),
    NOT_ACCEPTABLE("Not Acceptable"),
    CONFLICT("Conflict"),
    UNSUPPORTED_MEDIA_TYPE("Unsupported Media Type"),
    TOO_MANY_REQUESTS("Too Many Requests"),

    // --- Server Errors ---
    NOT_IMPLEMENTED("Not Implemented"),
    BAD_GATEWAY("Bad Gateway"),
    SERVICE_UNAVAILABLE("Service Unavailable"),
    GATEWAY_TIMEOUT("Gateway Timeout"),

    // --- System Failures ---
    DB_ERROR("Database Error"),
    IO_ERROR("I/O Error"),
    JSON_PARSE_ERROR("JSON Parse Error"),
    LOCK_FAILURE("Lock Acquisition Failure"),
    EXTERNAL_SERVICE_TIMEOUT("External Service Timeout"),

    // Domain Errors (Merged)
    INVALID_PRODUCT("Product ID cannot be empty."),
    INVALID_QUANTITY("Quantity must be greater than zero. Value: %d");

    private final String message;

    BusinessStatus(String message) {
        this.message = message;
    }
}
