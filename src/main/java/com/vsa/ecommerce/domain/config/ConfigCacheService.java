package com.vsa.ecommerce.domain.config;

import com.vsa.ecommerce.common.cache.CacheKeyConvention;
import com.vsa.ecommerce.common.cache.hybrid.HybridCacheManager;
import com.vsa.ecommerce.common.lock.DistributedLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static com.vsa.ecommerce.common.cache.CacheKeyConvention.RESOURCE_CONFIG;

/**
 * Example service demonstrating application configuration caching.
 * <p>
 * Caching Strategy:
 * - Long TTL (5 minutes)
 * - Versioned cache keys for breaking changes
 * - Distributed lock for cache rebuild
 * - Warm-up on application startup
 * <p>
 * Use Cases:
 * - Application settings
 * - System configuration
 * - Feature toggles
 * - API rate limits
 * <p>
 * Special Considerations:
 * - Config rarely changes, so longer TTLis acceptable
 * - Use distributed lock to prevent thundering herd on cache rebuild
 * - Version cache keys to support zero-downtime deployments
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigCacheService {

    private final HybridCacheManager cacheManager;
    private final CacheKeyConvention keyConvention;
    private final DistributedLockService lockService;
    // Inject your ConfigRepository here
    // private final ConfigRepository configRepository;

    private static final String CACHE_REBUILD_LOCK_KEY = "lock:cache:rebuild:config";

    /**
     * Get configuration value by key.
     * Example of distributed lock usage for cache rebuild.
     */
    public Optional<String> getConfig(String configKey) {
        if (configKey == null || configKey.isBlank()) {
            return Optional.empty();
        }

        String cacheKey = keyConvention.buildKey(RESOURCE_CONFIG, configKey);

        return Optional.ofNullable(
                cacheManager.getOrCompute(
                        cacheKey,
                        ConfigValue.class,
                        () -> loadConfigFromDatabase(configKey)))
                .map(ConfigValue::value);
    }

    /**
     * Get all configuration as a map.
     * Uses distributed lock to ensure only one instance rebuilds cache.
     */
    public Map<String, String> getAllConfig() {
        String cacheKey = keyConvention.buildKey(RESOURCE_CONFIG, "all");

        return cacheManager.getOrCompute(
                cacheKey,
                ConfigMap.class,
                this::loadAllConfigWithLock).configs();
    }

    /**
     * Update configuration and invalidate cache.
     */
    public void updateConfig(String configKey, String value) {
        if (configKey == null || value == null) {
            return;
        }

        // 1. Update database
        // configRepository.update(configKey, value);

        // 2. Invalidate specific key
        String cacheKey = keyConvention.buildKey(RESOURCE_CONFIG, configKey);
        cacheManager.evict(cacheKey);

        // 3. Also invalidate "all" cache
        String allCacheKey = keyConvention.buildKey(RESOURCE_CONFIG, "all");
        cacheManager.evict(allCacheKey);

        log.info("Config updated and cache invalidated: {}", configKey);
    }

    /**
     * Warm up configuration cache on application startup.
     * Uses distributed lock to ensure only one instance warms cache.
     */
    public void warmUpCache() {
        log.info("Warming up configuration cache...");

        Optional<ConfigMap> result = lockService.executeWithLock(
                CACHE_REBUILD_LOCK_KEY,
                () -> {
                    // Load and cache all configuration
                    ConfigMap configs = loadAllConfigFromDatabase();

                    String cacheKey = keyConvention.buildKey(RESOURCE_CONFIG, "all");
                    cacheManager.put(cacheKey, configs);

                    log.info("Configuration cache warmed up with {} entries", configs.configs().size());
                    return configs;
                });

        if (result.isEmpty()) {
            log.warn("Failed to acquire lock for cache warm-up (another instance may be warming up)");
        }
    }

    /**
     * Load all config with distributed lock protection.
     * Prevents thundering herd problem when cache expires.
     */
    private ConfigMap loadAllConfigWithLock() {
        Optional<ConfigMap> result = lockService.executeWithLock(
                CACHE_REBUILD_LOCK_KEY,
                this::loadAllConfigFromDatabase);

        return result.orElseGet(() -> {
            log.warn("Failed to acquire lock for config cache rebuild, returning empty");
            return new ConfigMap(Map.of());
        });
    }

    /**
     * Simulate loading single config from database.
     */
    private ConfigValue loadConfigFromDatabase(String configKey) {
        log.debug("Loading config from database: {}", configKey);

        // TODO: Replace with actual repository call
        // return configRepository.findByKey(configKey)
        // .map(ConfigValue::new)
        // .orElse(null);

        // Mock data
        return new ConfigValue("mock-value-" + configKey);
    }

    /**
     * Simulate loading all config from database.
     */
    private ConfigMap loadAllConfigFromDatabase() {
        log.debug("Loading all config from database");

        // TODO: Replace with actual repository call
        // Map<String, String> configs = configRepository.findAll()
        // .stream()
        // .collect(Collectors.toMap(Config::getKey, Config::getValue));

        // Mock data
        Map<String, String> configs = Map.of(
                "max-upload-size", "10MB",
                "session-timeout", "30m",
                "api-rate-limit", "100");

        return new ConfigMap(configs);
    }

    /**
     * DTOs for config data.
     */
    public record ConfigValue(String value) {
    }

    public record ConfigMap(Map<String, String> configs) {
    }
}
