package com.vsa.ecommerce.common.cache.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsa.ecommerce.common.cache.l1.LocalCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Subscriber for cache invalidation events via Redis Pub/Sub.
 * <p>
 * Listens to invalidation events published by other application instances
 * and clears local L1 cache accordingly.
 * <p>
 * This ensures L1 cache consistency across multiple instances in a
 * distributed deployment.
 * <p>
 * IMPORTANT: This subscriber only evicts from L1 (local cache).
 * L2 (Redis) is already up-to-date because the publisher instance
 * evicted from L2 before publishing the event.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheInvalidationSubscriber {

    private final LocalCacheService l1Cache;
    private final ObjectMapper objectMapper;

    /**
     * Handle incoming cache invalidation message.
     * Called by Redis message listener when an event is received.
     *
     * @param message JSON-serialized CacheInvalidationEvent
     */
    public void handleMessage(String message) {
        try {
            CacheInvalidationEvent event = objectMapper.readValue(message, CacheInvalidationEvent.class);

            log.debug("Received cache invalidation event: {}", event);

            switch (event.type()) {
                case SINGLE_KEY -> evictSingleKey(event.key());
                case PATTERN -> evictPattern(event.pattern());
                default -> log.warn("Unknown invalidation type: {}", event.type());
            }

        } catch (Exception e) {
            log.error("Error processing cache invalidation message: {}", message, e);
        }
    }

    /**
     * Evict a single key from L1 cache.
     */
    private void evictSingleKey(String key) {
        if (key == null || key.isBlank()) {
            return;
        }

        l1Cache.evict(key);
        log.debug("Evicted L1 cache key from Pub/Sub event: {}", key);
    }

    /**
     * Evict keys matching a pattern from L1 cache.
     * Since Caffeine doesn't support pattern-based eviction efficiently,
     * we clear all L1 cache. This is acceptable because:
     * - L1 has short TTL (30 seconds)
     * - L1 will rebuild from L2 naturally on next access
     */
    private void evictPattern(String pattern) {
        if (pattern == null || pattern.isBlank()) {
            return;
        }

        l1Cache.evictAll();
        log.info("Evicted ALL L1 cache entries due to pattern invalidation: {}", pattern);
    }
}
