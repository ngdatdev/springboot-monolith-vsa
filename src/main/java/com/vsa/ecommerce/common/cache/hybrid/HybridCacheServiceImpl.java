package com.vsa.ecommerce.common.cache.hybrid;

import com.vsa.ecommerce.common.cache.l1.LocalCacheService;
import com.vsa.ecommerce.common.cache.l2.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * Default implementation of Hybrid Cache Manager.
 * Coordinates L1 (Caffeine) and L2 (Redis).
 * Optimized with FusionCache features: Fail-Safe, Soft TTL, and Stampede
 * Protection.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HybridCacheServiceImpl implements HybridCacheService {

    private final LocalCacheService l1Cache;
    private final RedisCacheService l2Cache;

    // Local locks to prevent cache stampede on the same key
    private final ConcurrentHashMap<String, Object> keyLocks = new ConcurrentHashMap<>();

    // Executor for background refreshes (Soft TTL)
    private final ExecutorService refreshExecutor = Executors.newFixedThreadPool(10);

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        // Try L1
        Optional<CacheValue<T>> l1Container = l1Cache.get(key, CacheValue.class)
                .map(cv -> (CacheValue<T>) cv);
        if (l1Container.isPresent()) {
            return Optional.ofNullable(l1Container.get().getValue());
        }

        // Try L2
        Optional<CacheValue<T>> l2Container = l2Cache.get(key, CacheValue.class)
                .map(cv -> (CacheValue<T>) cv);
        if (l2Container.isPresent()) {
            l1Cache.put(key, l2Container.get());
            return Optional.ofNullable(l2Container.get().getValue());
        }

        return Optional.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getOrCompute(String key, Class<T> type, Supplier<T> supplier) {
        // 1. Try to get wrapper from cache
        Optional<CacheValue<T>> container = l1Cache.get(key, CacheValue.class)
                .map(cv -> (CacheValue<T>) cv);
        if (container.isEmpty()) {
            container = l2Cache.get(key, CacheValue.class)
                    .map(cv -> (CacheValue<T>) cv);
            container.ifPresent(c -> l1Cache.put(key, c));
        }

        if (container.isPresent()) {
            CacheValue<T> cv = container.get();

            // FusionCache Case: Soft TTL (Stale-While-Revalidate)
            if (cv.isSoftExpired()) {
                log.debug("Soft TTL expired for key: {}, triggering background refresh", key);
                triggerBackgroundRefresh(key, supplier);
            }

            return cv.getValue();
        }

        // 2. Cache Miss - Compute with Stampede Protection
        Object lock = keyLocks.computeIfAbsent(key, k -> new Object());
        synchronized (lock) {
            try {
                // Re-check after lock
                Optional<CacheValue<T>> current = l1Cache.get(key, CacheValue.class)
                        .map(cv -> (CacheValue<T>) cv);
                if (current.isPresent())
                    return current.get().getValue();

                return computeWithTimeout(key, supplier);
            } finally {
                keyLocks.remove(key);
            }
        }
    }

    private <T> T computeWithTimeout(String key, Supplier<T> supplier) {
        try {
            // FusionCache Case: Soft Timeout (Safe Get)
            // If DB is slow, return stale data if we have it
            return CompletableFuture.supplyAsync(() -> computeAndCache(key, supplier, true), refreshExecutor)
                    .get(2, TimeUnit.SECONDS); // 2 second timeout for "safe get"
        } catch (TimeoutException e) {
            log.warn("Compute timeout for key: {}. Attempting Fail-Safe fallback.", key);
            return (T) l2Cache.get(key, CacheValue.class)
                    .map(cv -> cv.getValue())
                    .orElseGet(() -> {
                        log.error("No stale data available for key: {} after timeout", key);
                        return null;
                    });
        } catch (Exception e) {
            log.error("Error during compute with timeout for key: {}", key, e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T computeAndCache(String key, Supplier<T> supplier, boolean isCritical) {
        try {
            T value = supplier.get();
            if (value != null) {
                put(key, value);
            }
            return value;
        } catch (Exception e) {
            // FusionCache Case: Fail-Safe
            // If compute fails, try to return STALE data from cache as fallback
            log.error("Failed to compute value for key: {}. Attempting Fail-Safe fallback.", key, e);
            Optional<CacheValue<T>> stale = l2Cache.get(key, CacheValue.class)
                    .map(cv -> (CacheValue<T>) cv);
            if (stale.isPresent()) {
                log.warn("FAIL-SAFE: Returning stale data for key: {}", key);
                return stale.get().getValue();
            }

            if (isCritical)
                throw new RuntimeException("Compute failed and no stale data available", e);
            return null;
        }
    }

    private <T> void triggerBackgroundRefresh(String key, Supplier<T> supplier) {
        // Prevent multiple background refreshes for the same key
        if (keyLocks.containsKey("refresh:" + key))
            return;

        keyLocks.put("refresh:" + key, new Object());
        refreshExecutor.submit(() -> {
            try {
                log.debug("Executing background refresh for key: {}", key);
                computeAndCache(key, supplier, false);
            } finally {
                keyLocks.remove("refresh:" + key);
            }
        });
    }

    @Override
    public <T> void put(String key, T value) {
        if (key == null || value == null)
            return;

        // Wrap value with Soft TTL metadata
        // For simplicity, we assume a default 5-minute soft TTL here,
        // but in a real app this should be configurable per key.
        long softTTL = System.currentTimeMillis() + (300 * 1000); // 5 minutes
        CacheValue<T> container = new CacheValue<>(value, softTTL);

        l1Cache.put(key, container);
        l2Cache.put(key, container);
    }

    @Override
    public void evict(String key) {
        if (key == null)
            return;

        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    performEvict(key);
                }
            });
        } else {
            performEvict(key);
        }
    }

    private void performEvict(String key) {
        l1Cache.evict(key);
        l2Cache.evict(key);
    }

    @Override
    public void evictPattern(String pattern) {
        if (pattern == null || pattern.isBlank())
            return;
        l1Cache.evictAll();
        l2Cache.evictPattern(pattern);
    }

    @Override
    public void evictAll() {
        l1Cache.evictAll();
        log.info("L1 Cache cleared. L2 relies on TTL.");
    }

    @Override
    public <T> boolean warmL1(String key, Class<T> type) {
        return l2Cache.get(key, CacheValue.class).map(val -> {
            l1Cache.put(key, val);
            return true;
        }).orElse(false);
    }
}
