package com.vsa.ecommerce.common.cache.l2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis-based implementation of L2 (Distributed) cache.
 * <p>
 * Features:
 * - JSON serialization for cross-platform compatibility
 * - Configurable TTL per key or default
 * - Pattern-based bulk operations
 * - Thread-safe operations
 * <p>
 * Performance:
 * - Latency: 5-10ms (network I/O)
 * - Throughput: Tens of thousands of ops/sec per instance
 * <p>
 * Configuration:
 * - cache.redis.ttl-minutes: Default time-to-live for cache entries
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisDistributedCacheService implements RedisCacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${cache.redis.ttl-minutes:5}")
    private int defaultTtlMinutes;

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        try {
            String json = redisTemplate.opsForValue().get(key);
            if (json == null) {
                log.trace("L2 Cache MISS: {}", key);
                return Optional.empty();
            }

            log.trace("L2 Cache HIT: {}", key);
            T value = objectMapper.readValue(json, type);
            return Optional.of(value);

        } catch (JsonProcessingException e) {
            log.error("Error deserializing from L2 cache for key: {}", key, e);
            evict(key);
            return Optional.empty();
        } catch (Exception e) {
            log.warn("Error retrieving from L2 cache for key: {}", key, e);
            return Optional.empty();
        }
    }

    @Override
    public <T> void put(String key, T value) {
        put(key, value, Duration.ofMinutes(defaultTtlMinutes));
    }

    @Override
    public <T> void put(String key, T value, Duration ttl) {
        if (key == null || value == null) {
            log.warn("Attempted to cache null key or value. Key: {}, Value: {}", key, value);
            return;
        }

        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, ttl);
            log.trace("L2 Cache PUT: {} (TTL: {})", key, ttl);
        } catch (JsonProcessingException e) {
            log.error("Error serializing value for L2 cache key: {}", key, e);
        } catch (Exception e) {
            log.error("Error putting value into L2 cache for key: {}", key, e);
        }
    }

    @Override
    public void evict(String key) {
        if (key == null) {
            return;
        }

        Boolean deleted = redisTemplate.delete(key);
        log.debug("L2 Cache EVICT: {} (deleted: {})", key, deleted);
    }

    @Override
    public long evictPattern(String pattern) {
        if (pattern == null || pattern.isBlank()) {
            log.warn("Attempted to evict with null or empty pattern");
            return 0;
        }

        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys == null || keys.isEmpty()) {
                log.debug("L2 Cache EVICT PATTERN: {} (no keys found)", pattern);
                return 0;
            }

            Long deleted = redisTemplate.delete(keys);
            long count = deleted != null ? deleted : 0;
            log.info("L2 Cache EVICT PATTERN: {} ({} keys deleted)", pattern, count);
            return count;

        } catch (Exception e) {
            log.error("Error evicting pattern from L2 cache: {}", pattern, e);
            return 0;
        }
    }

    @Override
    public long getKeysCount(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            return keys != null ? keys.size() : 0;
        } catch (Exception e) {
            log.error("Error counting keys for pattern: {}", pattern, e);
            return 0;
        }
    }

    @Override
    public Set<String> getKeys(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            log.debug("L2 Cache GET KEYS: {} ({} keys found)", pattern, keys != null ? keys.size() : 0);
            return keys != null ? keys : Set.of();
        } catch (Exception e) {
            log.error("Error getting keys for pattern: {}", pattern, e);
            return Set.of();
        }
    }

    @Override
    public boolean exists(String key) {
        if (key == null) {
            return false;
        }

        try {
            Boolean exists = redisTemplate.hasKey(key);
            return exists != null && exists;
        } catch (Exception e) {
            log.warn("Error checking existence for key: {}", key, e);
            return false;
        }
    }

    @Override
    public long getTtl(String key) {
        if (key == null) {
            return -1;
        }

        try {
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            return ttl != null ? ttl : -1;
        } catch (Exception e) {
            log.warn("Error getting TTL for key: {}", key, e);
            return -1;
        }
    }
}
