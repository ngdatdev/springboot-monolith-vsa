package com.vsa.ecommerce.common.abstraction;

import java.util.EnumMap;

import com.vsa.ecommerce.common.exception.BusinessStatus;
import org.springframework.http.HttpStatus;

/**
 * Maps internal BusinessCode enums to standard HTTP Status codes.
 * Decouples business logic from HTTP transport layer.
 */
public class HttpStatusMapper {

    private static final java.util.Map<BusinessStatus, HttpStatus> STATUS_MAP = new EnumMap<>(
            BusinessStatus.class);

    static {
        STATUS_MAP.put(BusinessStatus.SUCCESS, HttpStatus.OK);
        STATUS_MAP.put(BusinessStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        STATUS_MAP.put(BusinessStatus.NOT_FOUND, HttpStatus.NOT_FOUND);
        STATUS_MAP.put(BusinessStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);

        // Validation & Business Errors -> 400
        STATUS_MAP.put(BusinessStatus.PARAM_ERROR, HttpStatus.BAD_REQUEST);
        STATUS_MAP.put(BusinessStatus.FAILURE, HttpStatus.BAD_REQUEST);
        STATUS_MAP.put(BusinessStatus.BUSINESS_ERROR, HttpStatus.BAD_REQUEST);
        STATUS_MAP.put(BusinessStatus.INVALID_PRODUCT, HttpStatus.BAD_REQUEST);
        STATUS_MAP.put(BusinessStatus.INVALID_QUANTITY, HttpStatus.BAD_REQUEST);

        // --- Standard HTTP Errors ---
        STATUS_MAP.put(BusinessStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        STATUS_MAP.put(BusinessStatus.FORBIDDEN, HttpStatus.FORBIDDEN);
        STATUS_MAP.put(BusinessStatus.METHOD_NOT_ALLOWED, HttpStatus.METHOD_NOT_ALLOWED);
        STATUS_MAP.put(BusinessStatus.NOT_ACCEPTABLE, HttpStatus.NOT_ACCEPTABLE);
        STATUS_MAP.put(BusinessStatus.CONFLICT, HttpStatus.CONFLICT);
        STATUS_MAP.put(BusinessStatus.UNSUPPORTED_MEDIA_TYPE, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        STATUS_MAP.put(BusinessStatus.TOO_MANY_REQUESTS, HttpStatus.TOO_MANY_REQUESTS);

        // --- Server Errors ---
        STATUS_MAP.put(BusinessStatus.NOT_IMPLEMENTED, HttpStatus.NOT_IMPLEMENTED);
        STATUS_MAP.put(BusinessStatus.BAD_GATEWAY, HttpStatus.BAD_GATEWAY);
        STATUS_MAP.put(BusinessStatus.SERVICE_UNAVAILABLE, HttpStatus.SERVICE_UNAVAILABLE);
        STATUS_MAP.put(BusinessStatus.GATEWAY_TIMEOUT, HttpStatus.GATEWAY_TIMEOUT);

        // --- System Failures ---
        STATUS_MAP.put(BusinessStatus.DB_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        STATUS_MAP.put(BusinessStatus.IO_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        STATUS_MAP.put(BusinessStatus.JSON_PARSE_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        STATUS_MAP.put(BusinessStatus.LOCK_FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
        STATUS_MAP.put(BusinessStatus.EXTERNAL_SERVICE_TIMEOUT, HttpStatus.GATEWAY_TIMEOUT);
    }

    public static HttpStatus mapToStatus(BusinessStatus status) {
        return STATUS_MAP.getOrDefault(status, HttpStatus.BAD_REQUEST);
    }
}
