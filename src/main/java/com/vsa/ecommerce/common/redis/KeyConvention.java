package com.vsa.ecommerce.common.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Utility class for building standardized cache keys based on the active Spring
 * profile.
 * <p>
 * Cache Key Format: {profile}:{resource}:{identifier}
 * <p>
 * Examples:
 * - dev:user:12345
 * - prod:permission:read-orders
 */
@Slf4j
@Component
public class KeyConvention {

    private final String activeProfile;

    public KeyConvention(@Value("${spring.profiles.active:dev}") String activeProfile) {
        // Use first profile if multiple are provided
        this.activeProfile = activeProfile.split(",")[0].trim();
        log.info("CacheKeyConvention initialized with profile: {}", this.activeProfile);
    }

    // ========== Cache Key Resource Constants ==========

    // Core Entities
    public static final String RESOURCE_USER = "user";
    public static final String RESOURCE_ORDER = "order";
    public static final String RESOURCE_ORDER_ITEM = "order-item";
    public static final String RESOURCE_PRODUCT = "product";
    public static final String RESOURCE_PAYMENT = "payment";

    // Inventory Management
    public static final String RESOURCE_INVENTORY = "inventory";
    public static final String RESOURCE_INVENTORY_TRANSACTION = "inventory-transaction";

    // System Resources
    public static final String RESOURCE_NOTIFICATION = "notification";
    public static final String RESOURCE_PERMISSION = "permission";
    public static final String RESOURCE_CONFIG = "config";

    // Composite Keys (for complex queries)
    public static final String RESOURCE_USER_ORDERS = "user-orders";
    public static final String RESOURCE_PRODUCT_INVENTORY = "product-inventory";
    public static final String RESOURCE_ORDER_SUMMARY = "order-summary";

    // Security & Auth
    public static final String RESOURCE_SESSION = "session";
    public static final String RESOURCE_OTP = "otp";
    public static final String RESOURCE_OTP_ATTEMPT = "otp-attempt";
    public static final String RESOURCE_JWT_BLACKLIST = "jwt-blacklist";
    public static final String RESOURCE_LOGIN_ATTEMPT = "login-attempt";
    public static final String RESOURCE_LOGIN_LOCKOUT = "login-lockout";

    // Safety & Resiliency
    public static final String RESOURCE_RATE_LIMIT = "rate-limit";
    public static final String RESOURCE_IDEMPOTENCY = "idempotency";
    public static final String RESOURCE_FEATURE_FLAG = "feature-flag";

    /**
     * Build a cache key.
     * Format: {profile}:{resource}:{identifier}
     *
     * @param resource   Resource type
     * @param identifier Unique identifier
     * @return Formatted cache key
     */
    public String buildKey(String resource, String identifier) {
        String key = String.format("%s:%s:%s",
                activeProfile,
                resource,
                identifier);
        log.trace("Built cache key: {}", key);
        return key;
    }

    /**
     * Build a pattern for wildcard cache operations.
     *
     * @param resource Resource type
     * @return Pattern string with wildcard
     */
    public String buildPattern(String resource) {
        return String.format("%s:%s:*",
                activeProfile,
                resource);
    }

    /**
     * Parse cache key to extract resource type.
     *
     * @param cacheKey Full cache key
     * @return Resource type or null if invalid format
     */
    public String extractResource(String cacheKey) {
        if (cacheKey == null || cacheKey.isBlank()) {
            return null;
        }
        String[] parts = cacheKey.split(":");
        // Format: profile:res:id
        return parts.length >= 2 ? parts[1] : null;
    }

    /**
     * Parse cache key to extract identifier.
     *
     * @param cacheKey Full cache key
     * @return Identifier or null if invalid format
     */
    public String extractIdentifier(String cacheKey) {
        if (cacheKey == null || cacheKey.isBlank()) {
            return null;
        }
        String[] parts = cacheKey.split(":");
        // Last part is always the identifier
        return parts.length >= 3 ? parts[2] : parts[parts.length - 1];
    }
}
