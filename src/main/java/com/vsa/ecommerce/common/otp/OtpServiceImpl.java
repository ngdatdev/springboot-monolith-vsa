package com.vsa.ecommerce.common.otp;

import com.vsa.ecommerce.common.redis.KeyConvention;
import com.vsa.ecommerce.common.redis.BaseRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Optional;

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
public class OtpServiceImpl implements OtpService {

    private final BaseRedisService redisService;
    private final KeyConvention keyConvention;

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int DEFAULT_OTP_LENGTH = 6;
    private static final int MAX_VERIFICATION_ATTEMPTS = 5;
    private static final int OTP_TTL_MINUTES = 5;

    @Override
    public String generateOtp(String identifier) {
        var ttl = Duration.ofMinutes(OTP_TTL_MINUTES);
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("Identifier cannot be null or empty");
        }

        String otp = generateRandomOtp(DEFAULT_OTP_LENGTH);
        String key = keyConvention.buildKey(KeyConvention.RESOURCE_OTP, identifier);
        String attemptKey = keyConvention.buildKey(KeyConvention.RESOURCE_OTP_ATTEMPT, identifier);

        try {
            saveOtp(key, otp, ttl, attemptKey);

            log.info("OTP generated for: {} : {} (expires in {} seconds)", identifier, otp, ttl.getSeconds());
            return otp;

        } catch (Exception e) {
            log.error("Error generating OTP for: {}", identifier, e);
            throw new RuntimeException("Failed to generate OTP", e);
        }
    }

    @Override
    public boolean validateOtp(String identifier, String code) {
        if (identifier == null || identifier.isBlank() || code == null || code.isBlank()) {
            return false;
        }

        String key = keyConvention.buildKey(KeyConvention.RESOURCE_OTP, identifier);
        String attemptKey = keyConvention.buildKey(KeyConvention.RESOURCE_OTP_ATTEMPT, identifier);

        try {
            // Check attempt limit
            if (!isAttemptAllowed(attemptKey)) {
                log.warn("OTP verification attempts exceeded for: {}", identifier);
                return false;
            }

            // Get stored OTP
            String storedOtp = (String) redisService.get(key);

            if (storedOtp == null) {
                log.warn("OTP not found or expired for: {}", identifier);
                incrementAttempt(attemptKey);
                return false;
            }

            // Verify OTP
            boolean isValid = storedOtp.equals(code);

            if (isValid) {
                // Delete OTP after successful verification
                redisService.delete(key);
                redisService.delete(attemptKey);
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
    @Override
    public Optional<Long> getRemainingTtl(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            return Optional.empty();
        }

        String key = keyConvention.buildKey(KeyConvention.RESOURCE_OTP, identifier);

        try {
            Long ttl = redisService.getExpire(key);
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
    @Override
    public void invalidateOtp(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            return;
        }

        String key = keyConvention.buildKey(KeyConvention.RESOURCE_OTP, identifier);
        redisService.delete(key);
        log.info("OTP invalidated for: {}", identifier);
    }

    private void saveOtp(String key, String otp, Duration ttl, String attemptKey) {
        redisService.set(key, otp, ttl);
        redisService.delete(attemptKey);
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
            String attempts = (String) redisService.get(attemptKey);
            if (attempts == null) {
                return true;
            }
            return Integer.parseInt(attempts) < MAX_VERIFICATION_ATTEMPTS;
        } catch (Exception e) {
            return true;
        }
    }

    private void incrementAttempt(String attemptKey) {
        try {
            Long count = redisService.increment(attemptKey);
            if (count != null && count == 1) {
                redisService.expire(attemptKey, Duration.ofMinutes(15));
            }
        } catch (Exception e) {
            log.error("Error incrementing OTP attempt counter", e);
        }
    }
}
