package com.vsa.ecommerce.common.security.jwt;

import com.vsa.ecommerce.common.redis.KeyConvention;
import com.vsa.ecommerce.common.redis.BaseRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

/**
 * Service to manage JWT blacklisting using Redis.
 * Used during logout to invalidate tokens before their natural expiration.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final BaseRedisService redisService;
    private final KeyConvention keyConvention;

    /**
     * Blacklist a token until it naturally expires.
     *
     * @param token      JWT token to blacklist
     * @param expiration Natural expiration date of the token
     */
    public void blacklistToken(String token, Date expiration) {
        if (token == null || expiration == null)
            return;

        long ttlMillis = expiration.getTime() - System.currentTimeMillis();
        if (ttlMillis > 0) {
            String redisKey = keyConvention.buildKey(KeyConvention.RESOURCE_JWT_BLACKLIST, token);
            redisService.set(redisKey, "blacklisted", Duration.ofMillis(ttlMillis));
            log.debug("Token blacklisted in Redis. Key: {}, TTL: {}ms", redisKey, ttlMillis);
        }
    }

    /**
     * Check if a token is in the blacklist.
     *
     * @param token JWT token
     * @return true if blacklisted, false otherwise
     */
    public boolean isBlacklisted(String token) {
        if (token == null)
            return false;
        return redisService.hasKey(keyConvention.buildKey(KeyConvention.RESOURCE_JWT_BLACKLIST, token));
    }
}
