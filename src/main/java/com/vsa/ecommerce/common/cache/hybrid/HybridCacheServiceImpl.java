package com.vsa.ecommerce.common.cache.hybrid;

import com.vsa.ecommerce.common.cache.l1.LocalCacheService;
import com.vsa.ecommerce.common.cache.l2.RedisCacheService;
import com.vsa.ecommerce.common.cache.pubsub.CacheInvalidationPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Default implementation of Hybrid Cache Manager.
 * <p>
 * This is the PRIMARY cache service for production use.
 * Coordinates L1 (Caffeine) and L2 (Redis) with intelligent warm-up and
 * invalidation.
 * <p>
 * Key Features:
 * - Automatic L1 warm-up on L2 hits
 * - Distributed cache invalidation via Pub/Sub
 * - Cache-aside pattern support
 * - Thread-safe operations
 * <p>
 * Performance Metrics (typical):
 * - L1 hit: <1ms
 * - L2 hit + L1 warm: 5-10ms
 * - Cache miss + DB: 50-200ms
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HybridCacheServiceImpl implements HybridCacheService {

    private final LocalCacheService l1Cache;
    private final RedisCacheService l2Cache;
    private final CacheInvalidationPublisher invalidationPublisher;

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        // 1. Check L1 (Caffeine) first
        Optional<T> l1Value = l1Cache.get(key, type);
        if (l1Value.isPresent()) {
            log.trace("Hybrid Cache L1 HIT: {}", key);
            return l1Value;
        }

        // 2. Check L2 (Redis) if L1 miss
        Optional<T> l2Value = l2Cache.get(key, type);
        if (l2Value.isPresent()) {
            log.debug("Hybrid Cache L2 HIT (warming L1): {}", key);
            // Warm L1 from L2
            l1Cache.put(key, l2Value.get());
            return l2Value;
        }

        log.debug("Hybrid Cache MISS: {}", key);
        return Optional.empty();
    }

    @Override
    public <T> T getOrCompute(String key, Class<T> type, Supplier<T> supplier) {
        // Try to get from cache
        Optional<T> cached = get(key, type);
        if (cached.isPresent()) {
            return cached.get();
        }

        // Cache miss - compute value
        log.debug("Hybrid Cache COMPUTE: {}", key);
        T value = supplier.get();

        // Cache in both L1 and L2 if value is not null
        if (value != null) {
            put(key, value);
        }

        return value;
    }

    @Override
    public <T> void put(String key, T value) {
        if (key == null || value == null) {
            log.warn("Attempted to cache null key or value");
            return;
        }

        // Put in both L1 and L2
        l1Cache.put(key, value);
        l2Cache.put(key, value);
        log.debug("Hybrid Cache PUT: {}", key);
    }

    @Override
    public void evict(String key) {
        if (key == null) {
            return;
        }

        // Evict from both L1 and L2
        l1Cache.evict(key);
        l2Cache.evict(key);

        // Publish invalidation event to other instances
        invalidationPublisher.publishSingleKeyInvalidation(key);

        log.debug("Hybrid Cache EVICT (with Pub/Sub): {}", key);
    }

    @Override
    public void evictPattern(String pattern) {
        if (pattern == null || pattern.isBlank()) {
            log.warn("Attempted to evict with null or empty pattern");
            return;
        }

        // L1 doesn't support pattern-based eviction efficiently, so we clear all
        // This is acceptable because L1 has short TTL and will rebuild naturally
        l1Cache.evictAll();

        // L2 supports pattern-based eviction
        long evicted = l2Cache.evictPattern(pattern);

        // Publish pattern invalidation event to other instances
        invalidationPublisher.publishPatternInvalidation(pattern);

        log.info("Hybrid Cache EVICT PATTERN: {} ({} keys from L2, all from L1)", pattern, evicted);
    }

    @Override
    public void evictAll() {
        log.warn("Hybrid Cache EVICT ALL - This should rarely be used in production");

        l1Cache.evictAll();

        // For L2, we could evict all keys with namespace pattern, but this is dangerous
        // Instead, let TTL handle it. If you really need to clear all, implement
        // carefully.
        log.info("Hybrid Cache EVICT ALL - L1 cleared, L2 relies on TTL");
    }

    @Override
    public <T> boolean warmL1(String key, Class<T> type) {
        Optional<T> l2Value = l2Cache.get(key, type);
        if (l2Value.isPresent()) {
            l1Cache.put(key, l2Value.get());
            log.debug("Warmed L1 from L2: {}", key);
            return true;
        }
        return false;
    }
}
