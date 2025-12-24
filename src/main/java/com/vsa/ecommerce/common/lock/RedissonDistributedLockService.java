package com.vsa.ecommerce.common.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redisson-based implementation of distributed locking.
 * <p>
 * Features:
 * - Automatic lock renewal (prevents expiration during long operations)
 * - Fair locking (FIFO order)
 * - Automatic release on exception or completion
 * - Configurable timeout and lease time
 * <p>
 * Lock Key Recommendations:
 * - Use descriptive, hierarchical keys: "resource:action:identifier"
 * - Examples:
 * - "cache:rebuild:user-profile"
 * - "order:process:12345"
 * - "payment:charge:txn-abc"
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedissonDistributedLockService implements DistributedLockService {

    private final RedissonClient redissonClient;

    @Value("${cache.locks.wait-time-seconds:10}")
    private long defaultWaitTimeSeconds;

    @Value("${cache.locks.lease-time-seconds:30}")
    private long defaultLeaseTimeSeconds;

    @Override
    public <T> Optional<T> executeWithLock(String lockKey, Supplier<T> task) {
        return executeWithLock(
                lockKey,
                defaultWaitTimeSeconds * 1000,
                defaultLeaseTimeSeconds * 1000,
                task);
    }

    @Override
    public boolean executeWithLock(String lockKey, Runnable task) {
        Optional<Void> result = executeWithLock(
                lockKey,
                defaultWaitTimeSeconds * 1000,
                defaultLeaseTimeSeconds * 1000,
                () -> {
                    task.run();
                    return null;
                });
        return result.isPresent();
    }

    @Override
    public <T> Optional<T> executeWithLock(String lockKey, long waitTimeMs, long leaseTimeMs, Supplier<T> task) {
        if (lockKey == null || lockKey.isBlank()) {
            log.error("Lock key cannot be null or empty");
            return Optional.empty();
        }

        RLock lock = redissonClient.getLock(lockKey);

        try {
            // Try to acquire lock with timeout
            boolean acquired = lock.tryLock(waitTimeMs, leaseTimeMs, TimeUnit.MILLISECONDS);

            if (!acquired) {
                log.warn("Failed to acquire lock: {} (timeout after {}ms)", lockKey, waitTimeMs);
                return Optional.empty();
            }

            log.debug("Lock acquired: {}", lockKey);

            // Execute task while holding the lock
            T result = task.get();

            log.debug("Task completed successfully under lock: {}", lockKey);
            return Optional.ofNullable(result);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Lock acquisition interrupted for: {}", lockKey, e);
            return Optional.empty();

        } catch (Exception e) {
            log.error("Error executing task under lock: {}", lockKey, e);
            throw new RuntimeException("Distributed lock task execution failed", e);

        } finally {
            // Always release the lock if held by current thread
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("Lock released: {}", lockKey);
            }
        }
    }

    @Override
    public boolean tryLock(String lockKey, long waitTimeMs, long leaseTimeMs) {
        if (lockKey == null || lockKey.isBlank()) {
            log.error("Lock key cannot be null or empty");
            return false;
        }

        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean acquired = lock.tryLock(waitTimeMs, leaseTimeMs, TimeUnit.MILLISECONDS);
            if (acquired) {
                log.debug("Lock acquired manually: {}", lockKey);
            } else {
                log.debug("Failed to acquire lock manually: {}", lockKey);
            }
            return acquired;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Lock acquisition interrupted for: {}", lockKey, e);
            return false;
        }
    }

    @Override
    public void unlock(String lockKey) {
        if (lockKey == null || lockKey.isBlank()) {
            log.error("Lock key cannot be null or empty");
            return;
        }

        RLock lock = redissonClient.getLock(lockKey);

        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
            log.debug("Lock unlocked manually: {}", lockKey);
        } else {
            log.warn("Attempted to unlock a lock not held by current thread: {}", lockKey);
        }
    }

    @Override
    public boolean isLocked(String lockKey) {
        if (lockKey == null || lockKey.isBlank()) {
            return false;
        }

        RLock lock = redissonClient.getLock(lockKey);
        return lock.isLocked();
    }
}
