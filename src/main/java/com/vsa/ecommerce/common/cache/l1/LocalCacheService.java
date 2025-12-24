package com.vsa.ecommerce.common.cache.l1;

import com.github.benmanes.caffeine.cache.stats.CacheStats;

import java.util.Optional;

/**
 * Interface for L1 (Local/In-Memory) cache operations.
 * <p>
 * L1 cache characteristics:
 * - Very low latency (<1ms)
 * - Short TTL (10-30 seconds recommended)
 * - Not shared across instances
 * - Limited capacity (LRU/LFU eviction)
 * <p>
 * Use cases:
 * - Read-heavy, rarely-changing data (User profiles, Permissions, Config)
 * - Temporary computation results
 * - Session-scoped data
 * <p>
 * DO NOT use for:
 * - Transactional data
 * - Frequently updated entities
 * - Data requiring cross-instance consistency before eviction publish
 */
public interface LocalCacheService {

    /**
     * Get value from L1 cache.
     *
     * @param key  Cache key
     * @param type Value type class
     * @param <T>  Value type
     * @return Optional containing value if found, empty otherwise
     */
    <T> Optional<T> get(String key, Class<T> type);

    /**
     * Put value into L1 cache with default TTL.
     *
     * @param key   Cache key
     * @param value Value to cache
     * @param <T>   Value type
     */
    <T> void put(String key, T value);

    /**
     * Evict a specific key from L1 cache.
     *
     * @param key Cache key to evict
     */
    void evict(String key);

    /**
     * Evict all entries from L1 cache.
     */
    void evictAll();

    /**
     * Get cache statistics (hits, misses, evictions).
     *
     * @return CacheStats object
     */
    CacheStats getStats();

    /**
     * Get estimated cache size (number of entries).
     *
     * @return Approximate number of cached entries
     */
    long size();
}
