package com.vsa.ecommerce.common.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Utility class for building standardized cache keys with namespace, version,
 * and optional tenant ID.
 * <p>
 * Cache Key Format: {namespace}:{version}:{resource}:[{tenantId}]:{identifier}
 * <p>
 * Examples:
 * - vsa:v1:user::12345
 * - vsa:v1:permission:tenant-abc:read-orders
 * - vsa:v1:config::feature-flags
 */
@Slf4j
@Component
public class CacheKeyConvention {

    @Value("${cache.default-namespace:vsa}")
    private String defaultNamespace;

    @Value("${cache.default-version:v1}")
    private String defaultVersion;

    // ========== Cache Key Resource Constants ==========

    /**
     * Cache key resource names for domain entities.
     * Use these constants when building cache keys to ensure consistency.
     * 
     * Example usage:
     * 
     * <pre>
     * String key = buildKey(RESOURCE_USER, userId.toString());
     * // Result: "vsa:v1:user::123"
     * </pre>
     */

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

    // Session and Temporary Resources
    public static final String RESOURCE_SESSION = "session";
    public static final String RESOURCE_OTP = "otp";
    public static final String RESOURCE_FEATURE_FLAG = "feature-flag";
    public static final String RESOURCE_RATE_LIMIT = "rate-limit";

    /**
     * Build a cache key without tenant ID.
     *
     * @param resource   Resource type (e.g., "user", "permission", "config")
     * @param identifier Unique identifier (e.g., user ID, permission code)
     * @return Formatted cache key
     */
    public String buildKey(String resource, String identifier) {
        return buildKey(resource, identifier, null);
    }

    /**
     * Build a cache key with optional tenant ID.
     *
     * @param resource   Resource type (e.g., "user", "permission", "config")
     * @param identifier Unique identifier (e.g., user ID, permission code)
     * @param tenantId   Optional tenant ID for multi-tenant scenarios
     * @return Formatted cache key
     */
    public String buildKey(String resource, String identifier, String tenantId) {
        String tenant = tenantId != null ? tenantId : "";
        String key = String.format("%s:%s:%s:%s:%s",
                defaultNamespace,
                defaultVersion,
                resource,
                tenant,
                identifier);
        log.trace("Built cache key: {}", key);
        return key;
    }

    /**
     * Build a pattern for wildcard cache operations (e.g., eviction by resource
     * type).
     * <p>
     * Example: buildPattern("user") → "vsa:v1:user:*"
     *
     * @param resource Resource type
     * @return Pattern string with wildcard
     */
    public String buildPattern(String resource) {
        return String.format("%s:%s:%s:*",
                defaultNamespace,
                defaultVersion,
                resource);
    }

    /**
     * Build a pattern for a specific tenant's resources.
     *
     * @param resource Resource type
     * @param tenantId Tenant ID
     * @return Pattern string with wildcard for a tenant
     */
    public String buildTenantPattern(String resource, String tenantId) {
        return String.format("%s:%s:%s:%s:*",
                defaultNamespace,
                defaultVersion,
                resource,
                tenantId);
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
        return parts.length >= 3 ? parts[2] : null;
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
        return parts.length >= 5 ? parts[4] : null;
    }
}
