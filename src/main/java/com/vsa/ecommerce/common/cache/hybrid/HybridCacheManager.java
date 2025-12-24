package com.vsa.ecommerce.common.cache.hybrid;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Hybrid cache manager that coordinates L1 (local/Caffeine) and L2
 * (distributed/Redis) caches.
 * <p>
 * Lookup Strategy:
 * 1. Check L1 (Caffeine) → Return immediately if found
 * 2. Check L2 (Redis) → If found, warm L1 and return
 * 3. Return empty if both miss
 * <p>
 * Eviction Strategy:
 * - Evict from both L1 and L2
 * - Publish invalidation event via Pub/Sub (for multi-instance scenarios)
 * <p>
 * Performance Benefits:
 * - 95%+ L1 hit ratio → <1ms latency
 * - 3-5% L2 hit ratio → 5-10ms latency
 * - <1% database hit → 50-200ms latency
 * <p>
 * Use Case:
 * - Production-ready caching for read-heavy entities
 * - User profiles, permissions, configurations
 * - Multi-instance deployments
 */
public interface HybridCacheManager {

    /**
     * Get value from hybrid cache (L1 → L2 → empty).
     * If found in L2, warms L1 automatically.
     *
     * @param key  Cache key
     * @param type Value type class
     * @param <T>  Value type
     * @return Optional containing value if found, empty otherwise
     */
    <T> Optional<T> get(String key, Class<T> type);

    /**
     * Get value from cache or compute if missing (Cache-Aside pattern).
     * <p>
     * Flow:
     * 1. Check L1 → Return if found
     * 2. Check L2 → Warm L1 and return if found
     * 3. Execute supplier → Cache in both L1 and L2 → Return
     *
     * @param key      Cache key
     * @param type     Value type class
     * @param supplier Function to compute value if cache miss
     * @param <T>      Value type
     * @return Cached or computed value
     */
    <T> T getOrCompute(String key, Class<T> type, Supplier<T> supplier);

    /**
     * Put value into both L1 and L2 caches.
     *
     * @param key   Cache key
     * @param value Value to cache
     * @param <T>   Value type
     */
    <T> void put(String key, T value);

    /**
     * Evict a specific key from both L1 and L2 caches.
     * Publishes invalidation event to other instances.
     *
     * @param key Cache key to evict
     */
    void evict(String key);

    /**
     * Evict all keys matching a pattern from both L1 and L2.
     * Publishes invalidation event to other instances.
     *
     * @param pattern Cache key pattern (e.g., "vsa:v1:user:*")
     */
    void evictPattern(String pattern);

    /**
     * Evict all entries from both L1 and L2 caches.
     * WARNING: Use with extreme caution in production.
     */
    void evictAll();

    /**
     * Warm L1 cache from L2 for a specific key.
     * Useful for pre-loading frequently accessed data.
     *
     * @param key  Cache key
     * @param type Value type class
     * @param <T>  Value type
     * @return true if L1 was warmed, false if key not found in L2
     */
    <T> boolean warmL1(String key, Class<T> type);
}
