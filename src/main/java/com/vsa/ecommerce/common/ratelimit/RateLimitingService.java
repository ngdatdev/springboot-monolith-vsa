package com.vsa.ecommerce.common.ratelimit;

import com.vsa.ecommerce.common.redis.KeyConvention;
import com.vsa.ecommerce.common.redis.BaseRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Redis-based rate limiting service using sliding window algorithm.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitingService {

    private final BaseRedisService redisService;
    private final KeyConvention keyConvention;

    public boolean allowRequest(String identifier, int maxRequests, Duration window) {
        if (identifier == null || identifier.isBlank()) {
            log.warn("Rate limiting identifier cannot be null or empty");
            return false;
        }

        String key = keyConvention.buildKey(KeyConvention.RESOURCE_RATE_LIMIT, identifier);

        try {
            // Increment counter
            Long currentCount = redisService.increment(key);

            if (currentCount == null) {
                log.error("Failed to increment rate limit counter for: {}", identifier);
                return true; // Fail open - allow request if Redis operation fails
            }

            // Set expiration on first request
            if (currentCount == 1) {
                redisService.expire(key, window);
            }

            boolean allowed = currentCount <= maxRequests;

            if (!allowed) {
                log.warn("Rate limit exceeded for: {} (count: {}, max: {})", identifier, currentCount, maxRequests);
            } else {
                log.debug("Rate limit check passed for: {} (count: {}, max: {})", identifier, currentCount,
                        maxRequests);
            }

            return allowed;

        } catch (Exception e) {
            log.error("Error checking rate limit for: {}", identifier, e);
            return true; // Fail open - allow request on error
        }
    }

    /**
     * Get remaining requests for an identifier.
     *
     * @param identifier  Unique identifier
     * @param maxRequests Maximum number of requests allowed
     * @return Number of remaining requests, or maxRequests if no data
     */
    public int getRemainingRequests(String identifier, int maxRequests) {
        if (identifier == null || identifier.isBlank()) {
            return maxRequests;
        }

        String key = keyConvention.buildKey(KeyConvention.RESOURCE_RATE_LIMIT, identifier);

        try {
            String value = (String) redisService.get(key);
            if (value == null) {
                return maxRequests;
            }

            int currentCount = Integer.parseInt(value);
            return Math.max(0, maxRequests - currentCount);

        } catch (Exception e) {
            log.error("Error getting remaining requests for: {}", identifier, e);
            return maxRequests;
        }
    }

    /**
     * Reset rate limit for an identifier.
     *
     * @param identifier Unique identifier
     */
    public void reset(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            return;
        }

        String key = keyConvention.buildKey(KeyConvention.RESOURCE_RATE_LIMIT, identifier);
        redisService.delete(key);
        log.info("Rate limit reset for: {}", identifier);
    }
}
