package com.vsa.ecommerce.common.lock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for Distributed Lock Service.
 * <p>
 * Tests:
 * - Concurrent lock acquisition
 * - Automatic lock release
 * - Lock timeout handling
 * <p>
 * Requirements:
 * - Redis instance running on localhost:6379
 * - Redisson configured
 */
@SpringBootTest
class DistributedLockIntegrationTest {

    @Autowired
    private DistributedLockService lockService;

    @Test
    void testLockPreventsRaceCondition() throws InterruptedException {
        // Given
        String lockKey = "test:lock:counter";
        AtomicInteger counter = new AtomicInteger(0);
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        // When - multiple threads try to increment counter
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                lockService.executeWithLock(lockKey, () -> {
                    int current = counter.get();
                    // Simulate some processing
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    counter.set(current + 1);
                });
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        // Then - counter should be exactly threadCount (no race conditions)
        assertThat(counter.get()).isEqualTo(threadCount);
    }

    @Test
    void testExecuteWithLock_Success() {
        // Given
        String lockKey = "test:lock:task";

        // When
        Optional<String> result = lockService.executeWithLock(
                lockKey,
                () -> "Task completed");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo("Task completed");
        assertThat(lockService.isLocked(lockKey)).isFalse(); // Lock released
    }

    @Test
    void testLockTimeout() {
        // Given
        String lockKey = "test:lock:timeout";

        // First thread acquires lock
        boolean firstAcquired = lockService.tryLock(lockKey, 1000, 5000);
        assertThat(firstAcquired).isTrue();

        // When - second thread tries to acquire with short timeout
        Optional<String> result = lockService.executeWithLock(
                lockKey,
                100, // 100ms wait time
                1000,
                () -> "Should not execute");

        // Then - should timeout and return empty
        assertThat(result).isEmpty();

        // Cleanup
        lockService.unlock(lockKey);
    }

    @Test
    void testIsLocked() {
        // Given
        String lockKey = "test:lock:check";

        // When
        assertThat(lockService.isLocked(lockKey)).isFalse();

        boolean acquired = lockService.tryLock(lockKey, 1000, 5000);
        assertThat(acquired).isTrue();

        // Then
        assertThat(lockService.isLocked(lockKey)).isTrue();

        // Cleanup
        lockService.unlock(lockKey);
        assertThat(lockService.isLocked(lockKey)).isFalse();
    }
}
