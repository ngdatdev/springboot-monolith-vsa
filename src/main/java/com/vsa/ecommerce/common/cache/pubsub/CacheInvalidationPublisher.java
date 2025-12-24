package com.vsa.ecommerce.common.cache.pubsub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Publisher for cache invalidation events via Redis Pub/Sub.
 * <p>
 * When data is updated/deleted in one application instance, this publisher
 * broadcasts an invalidation event to all other instances, ensuring they
 * clear their local L1 caches.
 * <p>
 * This solves the consistency problem in multi-instance deployments where
 * L1 cache is not shared across instances.
 * <p>
 * Flow:
 * 1. Instance A updates user data
 * 2. Instance A evicts from its L1 and L2
 * 3. Instance A publishes invalidation event
 * 4. Instances B, C, D receive event and evict from their L1
 * 5. All instances now have consistent state
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheInvalidationPublisher {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${cache.pub-sub.channel:cache-invalidation}")
    private String channel;

    /**
     * Publish single key invalidation event.
     *
     * @param key Cache key to invalidate across all instances
     */
    public void publishSingleKeyInvalidation(String key) {
        if (key == null || key.isBlank()) {
            log.warn("Attempted to publish invalidation for null or empty key");
            return;
        }

        CacheInvalidationEvent event = CacheInvalidationEvent.single(key);
        publish(event);
    }

    /**
     * Publish pattern-based invalidation event.
     *
     * @param pattern Cache key pattern to invalidate across all instances
     */
    public void publishPatternInvalidation(String pattern) {
        if (pattern == null || pattern.isBlank()) {
            log.warn("Attempted to publish invalidation for null or empty pattern");
            return;
        }

        CacheInvalidationEvent event = CacheInvalidationEvent.pattern(pattern);
        publish(event);
    }

    /**
     * Publish cache invalidation event to Redis channel.
     *
     * @param event Invalidation event
     */
    private void publish(CacheInvalidationEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(channel, message);

            log.debug("Published cache invalidation event: {} on channel: {}", event, channel);

        } catch (JsonProcessingException e) {
            log.error("Error serializing cache invalidation event: {}", event, e);
        } catch (Exception e) {
            log.error("Error publishing cache invalidation event: {}", event, e);
        }
    }
}
