package com.vsa.ecommerce.common.cache.pubsub;

/**
 * Cache invalidation event model for Pub/Sub messaging.
 * <p>
 * Used to broadcast cache invalidation events across multiple application
 * instances.
 * When one instance updates/deletes data, it publishes an invalidation event,
 * and all other instances clear their local L1 cache accordingly.
 * <p>
 * Event Types:
 * - SINGLE_KEY: Invalidate a specific cache key (e.g., "vsa:v1:user::123")
 * - PATTERN: Invalidate all keys matching a pattern (e.g., "vsa:v1:user:*")
 *
 * @param key       Specific cache key to invalidate (for SINGLE_KEY type)
 * @param pattern   Pattern to match keys (for PATTERN type)
 * @param type      Invalidation type
 * @param timestamp Event timestamp (epoch milliseconds)
 */
public record CacheInvalidationEvent(
        String key,
        String pattern,
        Type type,
        long timestamp) {
    public enum Type {
        /**
         * Invalidate a single specific key.
         * Example: invalidate:user:123
         */
        SINGLE_KEY,

        /**
         * Invalidate all keys matching a pattern.
         * Example: invalidate:permission:*
         */
        PATTERN
    }

    /**
     * Create event for single key invalidation.
     */
    public static CacheInvalidationEvent single(String key) {
        return new CacheInvalidationEvent(key, null, Type.SINGLE_KEY, System.currentTimeMillis());
    }

    /**
     * Create event for pattern-based invalidation.
     */
    public static CacheInvalidationEvent pattern(String pattern) {
        return new CacheInvalidationEvent(null, pattern, Type.PATTERN, System.currentTimeMillis());
    }
}
