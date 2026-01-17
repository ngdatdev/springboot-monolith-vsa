package com.vsa.ecommerce.common.security;

import com.vsa.ecommerce.common.redis.KeyConvention;
import com.vsa.ecommerce.common.redis.BaseRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Service to track and manage failed login attempts in Redis.
 * Used for account lockout (brute-force protection).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final BaseRedisService redisService;
    private final KeyConvention keyConvention;

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME_MINUTES = 15;

    /**
     * Increment failed attempt count for an email.
     */
    public void loginFailed(String email) {
        String key = keyConvention.buildKey(KeyConvention.RESOURCE_LOGIN_ATTEMPT, email);
        Long count = redisService.increment(key);

        if (count != null && count == 1) {
            redisService.expire(key, Duration.ofMinutes(LOCK_TIME_MINUTES));
        }

        if (count != null && count >= MAX_ATTEMPTS) {
            lockAccount(email);
        }

        log.warn("Login failed for email: {} (Attempt {}/{})", email, count, MAX_ATTEMPTS);
    }

    /**
     * Reset attempt count (called on successful login).
     */
    public void loginSucceeded(String email) {
        redisService.delete(keyConvention.buildKey(KeyConvention.RESOURCE_LOGIN_ATTEMPT, email));
        redisService.delete(keyConvention.buildKey(KeyConvention.RESOURCE_LOGIN_LOCKOUT, email));
    }

    /**
     * Check if an account is currently blocked.
     */
    public boolean isBlocked(String email) {
        return redisService.hasKey(keyConvention.buildKey(KeyConvention.RESOURCE_LOGIN_LOCKOUT, email));
    }

    private void lockAccount(String email) {
        String key = keyConvention.buildKey(KeyConvention.RESOURCE_LOGIN_LOCKOUT, email);
        redisService.set(key, "true", Duration.ofMinutes(LOCK_TIME_MINUTES));
        log.error("Account locked for email: {} for {} minutes due to too many failed attempts", email,
                LOCK_TIME_MINUTES);
    }
}
