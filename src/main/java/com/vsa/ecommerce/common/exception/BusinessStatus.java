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
    USER_ALREADY_VERIFIED(400, "User already verified"),

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

    // Domain Errors
    INVALID_PRODUCT("Product ID cannot be empty."),
    INVALID_QUANTITY("Quantity must be greater than zero. Value: %d"),
    USER_NOT_FOUND(404, "User not found"),
    PRODUCT_NOT_FOUND(404, "Product not found"),
    ORDER_NOT_FOUND(404, "Order not found"),
    INSUFFICIENT_STOCK(400, "Insufficient stock"),
    INVALID_ORDER_STATUS_TRANSITION(400, "Invalid order status transition"),
    RESOURCE_NOT_FOUND(404, "Resource not found"),
    CART_EMPTY(400, "Cart is empty");

    private final int httpStatus; // Added httpStatus field
    private final String message;

    // Modified constructor to accept httpStatus
    BusinessStatus(int httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    // Overloaded constructor for existing enums without httpStatus
    BusinessStatus(String message) {
        this(0, message); // Default httpStatus to 0 or another appropriate value
    }
}
