package com.vsa.ecommerce.common.cache;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Cache statistics holder for monitoring cache performance.
 * Tracks hits, misses, evictions, and calculates hit ratio.
 * Thread-safe using atomic counters.
 */
@Slf4j
@Data
public class CacheMetrics {

    private final String cacheName;
    private final AtomicLong hits = new AtomicLong(0);
    private final AtomicLong misses = new AtomicLong(0);
    private final AtomicLong evictions = new AtomicLong(0);
    private final AtomicLong puts = new AtomicLong(0);

    public CacheMetrics(String cacheName) {
        this.cacheName = cacheName;
    }

    public void recordHit() {
        hits.incrementAndGet();
        logIfMilestone();
    }

    public void recordMiss() {
        misses.incrementAndGet();
        logIfMilestone();
    }

    public void recordEviction() {
        evictions.incrementAndGet();
    }

    public void recordPut() {
        puts.incrementAndGet();
    }

    public long getHits() {
        return hits.get();
    }

    public long getMisses() {
        return misses.get();
    }

    public long getEvictions() {
        return evictions.get();
    }

    public long getPuts() {
        return puts.get();
    }

    public long getTotalRequests() {
        return hits.get() + misses.get();
    }

    /**
     * Calculate cache hit ratio.
     *
     * @return Hit ratio (0.0 to 1.0), or 0.0 if no requests
     */
    public double getHitRatio() {
        long total = getTotalRequests();
        return total > 0 ? (double) hits.get() / total : 0.0;
    }

    /**
     * Get formatted hit ratio as percentage.
     *
     * @return Hit ratio percentage (0-100)
     */
    public double getHitRatioPercent() {
        return getHitRatio() * 100;
    }

    /**
     * Reset all metrics to zero.
     */
    public void reset() {
        hits.set(0);
        misses.set(0);
        evictions.set(0);
        puts.set(0);
        log.info("Cache metrics reset for: {}", cacheName);
    }

    /**
     * Log metrics at milestone intervals (every 100 requests).
     */
    private void logIfMilestone() {
        long total = getTotalRequests();
        if (total % 100 == 0) {
            log.info("[{}] Cache Stats - Hits: {}, Misses: {}, Hit Ratio: {:.2f}%, Total: {}",
                    cacheName, hits.get(), misses.get(), getHitRatioPercent(), total);
        }
    }

    @Override
    public String toString() {
        return String.format("CacheMetrics[%s: hits=%d, misses=%d, evictions=%d, puts=%d, hitRatio=%.2f%%]",
                cacheName, hits.get(), misses.get(), evictions.get(), puts.get(), getHitRatioPercent());
    }
}
