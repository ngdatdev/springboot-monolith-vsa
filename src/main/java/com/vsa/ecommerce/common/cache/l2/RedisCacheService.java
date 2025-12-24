package com.vsa.ecommerce.common.cache.l2;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

/**
 * Interface for L2 (Distributed/Redis) cache operations.
 * <p>
 * L2 cache characteristics:
 * - Low latency (5-10ms typical)
 * - Longer TTL than L1 (2-10 minutes recommended)
 * - Shared across all application instances
 * - Requires serialization (JSON recommended)
 * <p>
 * Use cases:
 * - Multi-instance data sharing
 * - Slightly more durable caching than L1
 * - Cross-service data (in microservices)
 * - Session storage
 * <p>
 * DO NOT use for:
 * - Highly transactional data
 * - Data requiring ACID guarantees
 * - Large binary objects (>1MB) - use object storage instead
 */
public interface RedisCacheService {

    /**
     * Get value from Redis cache.
     *
     * @param key  Cache key
     * @param type Value type class
     * @param <T>  Value type
     * @return Optional containing value if found, empty otherwise
     */
    <T> Optional<T> get(String key, Class<T> type);

    /**
     * Put value into Redis cache with default TTL.
     *
     * @param key   Cache key
     * @param value Value to cache
     * @param <T>   Value type
     */
    <T> void put(String key, T value);

    /**
     * Put value into Redis cache with custom TTL.
     *
     * @param key   Cache key
     * @param value Value to cache
     * @param ttl   Time-to-live duration
     * @param <T>   Value type
     */
    <T> void put(String key, T value, Duration ttl);

    /**
     * Evict a specific key from Redis cache.
     *
     * @param key Cache key to evict
     */
    void evict(String key);

    /**
     * Evict keys matching a pattern (e.g., "vsa:v1:user:*").
     * Use with caution in production - can be slow with many keys.
     *
     * @param pattern Redis key pattern (supports wildcards)
     * @return Number of keys evicted
     */
    long evictPattern(String pattern);

    /**
     * Get count of keys matching a pattern.
     *
     * @param pattern Redis key pattern
     * @return Number of matching keys
     */
    long getKeysCount(String pattern);

    /**
     * Get all keys matching a pattern.
     * WARNING: Use only for debugging/admin operations. Can be slow with large
     * datasets.
     *
     * @param pattern Redis key pattern
     * @return Set of matching keys
     */
    Set<String> getKeys(String pattern);

    /**
     * Check if a key exists in Redis cache.
     *
     * @param key Cache key
     * @return true if key exists, false otherwise
     */
    boolean exists(String key);

    /**
     * Get remaining TTL for a key.
     *
     * @param key Cache key
     * @return Remaining TTL in seconds, or -1 if key doesn't exist, -2 if no
     *         expiration
     */
    long getTtl(String key);
}
