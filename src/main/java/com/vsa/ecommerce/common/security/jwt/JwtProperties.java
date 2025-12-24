package com.vsa.ecommerce.common.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for JWT.
 * Maps to security.jwt.* in application.yml
 */
@Component
@ConfigurationProperties(prefix = "security.jwt")
@Getter
@Setter
public class JwtProperties {

    /**
     * Secret key for signing JWT tokens.
     * MUST be at least 256 bits (32 bytes) for HS256.
     * Should be stored in environment variable in production.
     */
    private String secretKey;

    /**
     * Access token expiration in milliseconds.
     * Default: 1 hour (3600000 ms)
     */
    private Long expirationMs = 3600000L;

    /**
     * Refresh token expiration in milliseconds.
     * Default: 24 hours (86400000 ms)
     */
    private Long refreshExpirationMs = 86400000L;

    /**
     * Token issuer (who issued the token).
     * Default: vsa-ecommerce
     */
    private String issuer = "vsa-ecommerce";
}
