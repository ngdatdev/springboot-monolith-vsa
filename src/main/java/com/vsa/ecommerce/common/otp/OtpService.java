package com.vsa.ecommerce.common.otp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * One-Time Password (OTP) and temporary token management service using Redis.
 * <p>
 * Use Cases:
 * - Email verification codes
 * - SMS verification codes
 * - Password reset tokens
 * - Temporary access tokens
 * - Two-factor authentication codes
 * <p>
 * Features:
 * - Automatic expiration (TTL-based)
 * - Secure random generation
 * - Attempt limiting (prevent brute force)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int DEFAULT_OTP_LENGTH = 6;
    private static final int MAX_VERIFICATION_ATTEMPTS = 5;

    /**
     * Generate and store an OTP with default 5-minute expiration.
     *
     * @param identifier Unique identifier (e.g., email, user ID)
     * @return Generated OTP code
     */
    public String generateOtp(String identifier) {
        return generateOtp(identifier, Duration.ofMinutes(5));
    }

    /**
     * Generate and store an OTP with custom expiration.
     *
     * @param identifier Unique identifier
     * @param ttl        Time-to-live duration
     * @return Generated OTP code
     */
    public String generateOtp(String identifier, Duration ttl) {
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("Identifier cannot be null or empty");
        }

        String otp = generateRandomOtp(DEFAULT_OTP_LENGTH);
        String key = buildOtpKey(identifier);
        String attemptKey = buildAttemptKey(identifier);

        try {
            // Store OTP with TTL
            redisTemplate.opsForValue().set(key, otp, ttl);

            // Reset attempt counter
            redisTemplate.delete(attemptKey);

            log.info("OTP generated for: {} (expires in {} seconds)", identifier, ttl.getSeconds());
            return otp;

        } catch (Exception e) {
            log.error("Error generating OTP for: {}", identifier, e);
            throw new RuntimeException("Failed to generate OTP", e);
        }
    }

    /**
     * Verify an OTP code.
     *
     * @param identifier Unique identifier
     * @param code       OTP code to verify
     * @return true if code is valid, false otherwise
     */
    public boolean verifyOtp(String identifier, String code) {
        if (identifier == null || identifier.isBlank() || code == null || code.isBlank()) {
            return false;
        }

        String key = buildOtpKey(identifier);
        String attemptKey = buildAttemptKey(identifier);

        try {
            // Check attempt limit
            if (!isAttemptAllowed(attemptKey)) {
                log.warn("OTP verification attempts exceeded for: {}", identifier);
                return false;
            }

            // Get stored OTP
            String storedOtp = redisTemplate.opsForValue().get(key);

            if (storedOtp == null) {
                log.debug("OTP not found or expired for: {}", identifier);
                incrementAttempt(attemptKey);
                return false;
            }

            // Verify OTP
            boolean isValid = storedOtp.equals(code);

            if (isValid) {
                // Delete OTP after successful verification
                redisTemplate.delete(key);
                redisTemplate.delete(attemptKey);
                log.info("OTP verified successfully for: {}", identifier);
            } else {
                incrementAttempt(attemptKey);
                log.warn("Invalid OTP attempt for: {}", identifier);
            }

            return isValid;

        } catch (Exception e) {
            log.error("Error verifying OTP for: {}", identifier, e);
            return false;
        }
    }

    /**
     * Get remaining TTL for an OTP.
     *
     * @param identifier Unique identifier
     * @return Optional containing remaining seconds, empty if OTP doesn't exist
     */
    public Optional<Long> getRemainingTtl(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            return Optional.empty();
        }

        String key = buildOtpKey(identifier);

        try {
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            return (ttl != null && ttl > 0) ? Optional.of(ttl) : Optional.empty();
        } catch (Exception e) {
            log.error("Error getting OTP TTL for: {}", identifier, e);
            return Optional.empty();
        }
    }

    /**
     * Invalidate/delete an OTP.
     *
     * @param identifier Unique identifier
     */
    public void invalidateOtp(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            return;
        }

        String key = buildOtpKey(identifier);
        redisTemplate.delete(key);
        log.info("OTP invalidated for: {}", identifier);
    }

    private String generateRandomOtp(int length) {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(RANDOM.nextInt(10));
        }
        return otp.toString();
    }

    private boolean isAttemptAllowed(String attemptKey) {
        try {
            String attempts = redisTemplate.opsForValue().get(attemptKey);
            if (attempts == null) {
                return true;
            }
            return Integer.parseInt(attempts) < MAX_VERIFICATION_ATTEMPTS;
        } catch (Exception e) {
            return true; // Fail open
        }
    }

    private void incrementAttempt(String attemptKey) {
        try {
            Long count = redisTemplate.opsForValue().increment(attemptKey);
            if (count != null && count == 1) {
                // Set expiration on first attempt (15 minutes)
                redisTemplate.expire(attemptKey, 15, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            log.error("Error incrementing OTP attempt counter", e);
        }
    }

    private String buildOtpKey(String identifier) {
        return String.format("otp:%s", identifier);
    }

    private String buildAttemptKey(String identifier) {
        return String.format("otp:attempts:%s", identifier);
    }
}
