package com.vsa.ecommerce.common.featureflag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Dynamic feature flag service using Redis.
 * <p>
 * Allows real-time feature enabling/disabling without deployment.
 * Supports per-user, per-tenant, and global feature flags.
 * <p>
 * Use Cases:
 * - A/B testing
 * - Gradual rollouts
 * - Emergency kill switches
 * - Tenant-specific features
 * - Beta feature access
 * <p>
 * Flag Hierarchy (priority order):
 * 1. User-specific flag
 * 2. Tenant-specific flag
 * 3. Global flag
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeatureFlagService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Check if a feature is enabled (global check).
     *
     * @param featureName Feature name
     * @return true if enabled, false otherwise (default: false)
     */
    public boolean isEnabled(String featureName) {
        return isEnabled(featureName, null, null);
    }

    /**
     * Check if a feature is enabled for a specific tenant.
     *
     * @param featureName Feature name
     * @param tenantId    Tenant ID
     * @return true if enabled, false otherwise
     */
    public boolean isEnabledForTenant(String featureName, String tenantId) {
        return isEnabled(featureName, tenantId, null);
    }

    /**
     * Check if a feature is enabled for a specific user.
     * Checks in order: user → tenant → global.
     *
     * @param featureName Feature name
     * @param tenantId    Tenant ID (optional)
     * @param userId      User ID
     * @return true if enabled at any level, false otherwise
     */
    public boolean isEnabled(String featureName, String tenantId, String userId) {
        if (featureName == null || featureName.isBlank()) {
            return false;
        }

        try {
            // 1. Check user-specific flag
            if (userId != null && !userId.isBlank()) {
                Optional<Boolean> userFlag = getFlagValue(buildUserKey(featureName, userId));
                if (userFlag.isPresent()) {
                    log.debug("Feature '{}' user-specific flag: {}", featureName, userFlag.get());
                    return userFlag.get();
                }
            }

            // 2. Check tenant-specific flag
            if (tenantId != null && !tenantId.isBlank()) {
                Optional<Boolean> tenantFlag = getFlagValue(buildTenantKey(featureName, tenantId));
                if (tenantFlag.isPresent()) {
                    log.debug("Feature '{}' tenant-specific flag: {}", featureName, tenantFlag.get());
                    return tenantFlag.get();
                }
            }

            // 3. Check global flag
            Optional<Boolean> globalFlag = getFlagValue(buildGlobalKey(featureName));
            boolean enabled = globalFlag.orElse(false);
            log.debug("Feature '{}' global flag: {}", featureName, enabled);
            return enabled;

        } catch (Exception e) {
            log.error("Error checking feature flag: {}", featureName, e);
            return false; // Fail closed - safer to disable feature on error
        }
    }

    /**
     * Enable a feature globally.
     */
    public void enableGlobal(String featureName) {
        setFlag(buildGlobalKey(featureName), true);
        log.info("Feature enabled globally: {}", featureName);
    }

    /**
     * Disable a feature globally.
     */
    public void disableGlobal(String featureName) {
        setFlag(buildGlobalKey(featureName), false);
        log.info("Feature disabled globally: {}", featureName);
    }

    /**
     * Enable a feature for a specific tenant.
     */
    public void enableForTenant(String featureName, String tenantId) {
        setFlag(buildTenantKey(featureName, tenantId), true);
        log.info("Feature '{}' enabled for tenant: {}", featureName, tenantId);
    }

    /**
     * Disable a feature for a specific tenant.
     */
    public void disableForTenant(String featureName, String tenantId) {
        setFlag(buildTenantKey(featureName, tenantId), false);
        log.info("Feature '{}' disabled for tenant: {}", featureName, tenantId);
    }

    /**
     * Enable a feature for a specific user.
     */
    public void enableForUser(String featureName, String userId) {
        setFlag(buildUserKey(featureName, userId), true);
        log.info("Feature '{}' enabled for user: {}", featureName, userId);
    }

    /**
     * Disable a feature for a specific user.
     */
    public void disableForUser(String featureName, String userId) {
        setFlag(buildUserKey(featureName, userId), false);
        log.info("Feature '{}' disabled for user: {}", featureName, userId);
    }

    /**
     * Get all feature flags (for admin/debugging).
     */
    public Map<String, Boolean> getAllFlags() {
        try {
            Set<String> keys = redisTemplate.keys("feature-flag:*");
            if (keys == null || keys.isEmpty()) {
                return Map.of();
            }

            Map<String, Boolean> flags = new java.util.HashMap<>();
            for (String key : keys) {
                getFlagValue(key).ifPresent(value -> flags.put(key, value));
            }
            return flags;

        } catch (Exception e) {
            log.error("Error getting all feature flags", e);
            return Map.of();
        }
    }

    private Optional<Boolean> getFlagValue(String key) {
        try {
            String value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return Optional.empty();
            }
            return Optional.of(Boolean.parseBoolean(value));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private void setFlag(String key, boolean value) {
        redisTemplate.opsForValue().set(key, String.valueOf(value));
    }

    private String buildGlobalKey(String featureName) {
        return String.format("feature-flag:global:%s", featureName);
    }

    private String buildTenantKey(String featureName, String tenantId) {
        return String.format("feature-flag:tenant:%s:%s", tenantId, featureName);
    }

    private String buildUserKey(String featureName, String userId) {
        return String.format("feature-flag:user:%s:%s", userId, featureName);
    }
}
