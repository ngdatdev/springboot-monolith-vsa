package com.vsa.ecommerce.common.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Service to track and manage failed login attempts in Redis.
 * Used for account lockout (brute-force protection).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME_MINUTES = 15;
    private static final String LOCKOUT_PREFIX = "login_lockout:";
    private static final String ATTEMPT_PREFIX = "login_attempts:";

    /**
     * Increment failed attempt count for an email.
     */
    public void loginFailed(String email) {
        String key = ATTEMPT_PREFIX + email;
        Long count = redisTemplate.opsForValue().increment(key);

        if (count != null && count == 1) {
            redisTemplate.expire(key, LOCK_TIME_MINUTES, TimeUnit.MINUTES);
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
        redisTemplate.delete(ATTEMPT_PREFIX + email);
        redisTemplate.delete(LOCKOUT_PREFIX + email);
    }

    /**
     * Check if an account is currently blocked.
     */
    public boolean isBlocked(String email) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(LOCKOUT_PREFIX + email));
    }

    private void lockAccount(String email) {
        redisTemplate.opsForValue().set(LOCKOUT_PREFIX + email, "true", LOCK_TIME_MINUTES, TimeUnit.MINUTES);
        log.error("Account locked for email: {} for {} minutes due to too many failed attempts", email,
                LOCK_TIME_MINUTES);
    }
}
