package com.vsa.ecommerce.common.cache;

import org.springframework.http.ResponseEntity;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Utility for handling HTTP caching headers.
 */
public class CacheUtils {

    /**
     * Checks if the resource has been modified based on the 'If-Modified-Since'
     * header.
     * 
     * @param lastModified    Current last modified time of the resource
     * @param ifModifiedSince Header value from the request
     * @return true if modified, false otherwise
     */
    public static boolean isNotModified(ZonedDateTime lastModified, ZonedDateTime ifModifiedSince) {
        if (ifModifiedSince == null || lastModified == null) {
            return true;
        }
        // HTTP dates have second precision
        return lastModified.truncatedTo(ChronoUnit.SECONDS)
                .isAfter(ifModifiedSince.truncatedTo(ChronoUnit.SECONDS));
    }

    /**
     * Builder for ResponseEntity with Last-Modified header.
     */
    public static <T> ResponseEntity.BodyBuilder lastModified(ZonedDateTime lastModified) {
        return ResponseEntity.ok()
                .lastModified(lastModified.toInstant());
    }

    /**
     * Creates a 304 Not Modified response.
     */
    public static <T> ResponseEntity<T> notModified() {
        return ResponseEntity.status(304).build();
    }
}
