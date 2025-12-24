package com.vsa.ecommerce.common.ratelimit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Redis-based rate limiting service using sliding window algorithm.
 * <p>
 * Use Cases:
 * - API rate limiting (e.g., 100 requests per minute per user)
 * - Login attempt limiting (e.g., 5 failed attempts per 15 minutes)
 * - Email sending limiting (e.g., 10 emails per hour)
 * - Resource access throttling
 * <p>
 * Algorithm: Sliding Window Counter
 * - More accurate than fixed window
 * - Lower memory footprint than sliding log
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitingService {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Check if request is allowed based on rate limit.
     * Increments counter if allowed.
     *
     * @param identifier  Unique identifier (e.g., user ID, IP address)
     * @param maxRequests Maximum number of requests allowed
     * @param window      Time window duration
     * @return true if request is allowed, false if rate limit exceeded
     */
    public boolean allowRequest(String identifier, int maxRequests, Duration window) {
        if (identifier == null || identifier.isBlank()) {
            log.warn("Rate limiting identifier cannot be null or empty");
            return false;
        }

        String key = buildRateLimitKey(identifier);

        try {
            // Increment counter
            Long currentCount = redisTemplate.opsForValue().increment(key);

            if (currentCount == null) {
                log.error("Failed to increment rate limit counter for: {}", identifier);
                return true; // Fail open - allow request if Redis operation fails
            }

            // Set expiration on first request
            if (currentCount == 1) {
                redisTemplate.expire(key, window.getSeconds(), TimeUnit.SECONDS);
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

        String key = buildRateLimitKey(identifier);

        try {
            String value = redisTemplate.opsForValue().get(key);
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

        String key = buildRateLimitKey(identifier);
        redisTemplate.delete(key);
        log.info("Rate limit reset for: {}", identifier);
    }

    private String buildRateLimitKey(String identifier) {
        return String.format("rate-limit:%s", identifier);
    }
}
