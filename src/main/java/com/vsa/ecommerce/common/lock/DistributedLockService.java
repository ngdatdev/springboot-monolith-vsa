package com.vsa.ecommerce.common.lock;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Interface for distributed locking operations.
 * <p>
 * Distributed locks are essential in multi-instance deployments to prevent race
 * conditions
 * when multiple instances try to perform the same operation concurrently.
 * <p>
 * Use Cases:
 * - Cache warm-up/rebuild (ensure only one instance rebuilds)
 * - Idempotent request processing (prevent duplicate processing)
 * - Order processing (prevent double-debit)
 * - Resource creation (prevent duplicates)
 * - Scheduled jobs (ensure single execution across cluster)
 * - Critical section protection in distributed systems
 * <p>
 * Implementation:
 * Uses Redisson's distributed lock with automatic renewal and expiration.
 */
public interface DistributedLockService {

    /**
     * Execute a task with distributed lock protection.
     * Blocks until lock is acquired or timeout is reached.
     * Automatically releases lock after execution or on exception.
     *
     * @param lockKey Unique lock key (e.g., "order:process:12345")
     * @param task    Task to execute
     * @param <T>     Return type
     * @return Optional containing result if lock acquired and task executed, empty
     *         if timeout
     */
    <T> Optional<T> executeWithLock(String lockKey, Supplier<T> task);

    /**
     * Execute a void task with distributed lock protection.
     *
     * @param lockKey Unique lock key
     * @param task    Task to execute
     * @return true if lock acquired and task executed, false if timeout
     */
    boolean executeWithLock(String lockKey, Runnable task);

    /**
     * Execute a task with custom lock timeout configuration.
     *
     * @param lockKey     Unique lock key
     * @param waitTimeMs  Maximum time to wait for lock acquisition (milliseconds)
     * @param leaseTimeMs Lock auto-release time (milliseconds)
     * @param task        Task to execute
     * @param <T>         Return type
     * @return Optional containing result if lock acquired and task executed, empty
     *         if timeout
     */
    <T> Optional<T> executeWithLock(String lockKey, long waitTimeMs, long leaseTimeMs, Supplier<T> task);

    /**
     * Try to acquire a lock without executing any task.
     * Useful for conditional logic based on lock availability.
     *
     * @param lockKey     Unique lock key
     * @param waitTimeMs  Maximum time to wait for lock acquisition (milliseconds)
     * @param leaseTimeMs Lock auto-release time (milliseconds)
     * @return true if lock acquired, false otherwise
     */
    boolean tryLock(String lockKey, long waitTimeMs, long leaseTimeMs);

    /**
     * Manually unlock a previously acquired lock.
     * WARNING: Use carefully - typically locks are auto-released by executeWithLock
     * methods.
     *
     * @param lockKey Lock key to unlock
     */
    void unlock(String lockKey);

    /**
     * Check if a lock is currently held.
     *
     * @param lockKey Lock key to check
     * @return true if lock is held by any instance, false otherwise
     */
    boolean isLocked(String lockKey);
}
