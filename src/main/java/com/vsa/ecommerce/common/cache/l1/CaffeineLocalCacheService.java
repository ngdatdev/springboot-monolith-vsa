package com.vsa.ecommerce.common.cache.l1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Caffeine-based implementation of L1 (Local/In-Memory) cache.
 * <p>
 * Features:
 * - Window TinyLFU eviction policy for optimal hit ratio
 * - Configurable TTL and maximum size
 * - Thread-safe operations
 * - Built-in statistics tracking
 * <p>
 * Performance:
 * - Latency: <1ms (in-memory)
 * - Throughput: Millions of ops/sec
 * <p>
 * Configuration:
 * - cache.caffeine.ttl-seconds: Time-to-live for cache entries
 * - cache.caffeine.max-size: Maximum number of entries
 */
@Slf4j
@Service
public class CaffeineLocalCacheService implements LocalCacheService {

    private final Cache<String, Object> cache;
    private final ObjectMapper objectMapper;

    public CaffeineLocalCacheService(
            ObjectMapper objectMapper,
            @Value("${cache.caffeine.ttl-seconds:30}") int ttlSeconds,
            @Value("${cache.caffeine.max-size:1000}") int maxSize) {
        this.objectMapper = objectMapper;
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(ttlSeconds, TimeUnit.SECONDS)
                .maximumSize(maxSize)
                .recordStats()
                .build();

        log.info("Caffeine L1 Cache initialized - TTL: {}s, Max Size: {}", ttlSeconds, maxSize);
    }

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        try {
            Object value = cache.getIfPresent(key);
            if (value == null) {
                log.trace("L1 Cache MISS: {}", key);
                return Optional.empty();
            }

            log.trace("L1 Cache HIT: {}", key);

            // Handle type casting safely
            if (type.isInstance(value)) {
                return Optional.of(type.cast(value));
            }

            // If stored as different type, try conversion via ObjectMapper
            T converted = objectMapper.convertValue(value, type);
            return Optional.of(converted);

        } catch (Exception e) {
            log.warn("Error retrieving from L1 cache for key: {}", key, e);
            cache.invalidate(key);
            return Optional.empty();
        }
    }

    @Override
    public <T> void put(String key, T value) {
        if (key == null || value == null) {
            log.warn("Attempted to cache null key or value. Key: {}, Value: {}", key, value);
            return;
        }

        try {
            cache.put(key, value);
            log.trace("L1 Cache PUT: {}", key);
        } catch (Exception e) {
            log.error("Error putting value into L1 cache for key: {}", key, e);
        }
    }

    @Override
    public void evict(String key) {
        if (key == null) {
            return;
        }

        cache.invalidate(key);
        log.debug("L1 Cache EVICT: {}", key);
    }

    @Override
    public void evictAll() {
        long sizeBefore = cache.estimatedSize();
        cache.invalidateAll();
        log.info("L1 Cache EVICT ALL - Cleared {} entries", sizeBefore);
    }

    @Override
    public CacheStats getStats() {
        return cache.stats();
    }

    @Override
    public long size() {
        return cache.estimatedSize();
    }
}
