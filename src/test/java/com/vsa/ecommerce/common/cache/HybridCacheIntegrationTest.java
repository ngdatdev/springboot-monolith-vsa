package com.vsa.ecommerce.common.cache;

import com.vsa.ecommerce.common.cache.hybrid.HybridCacheService;
import com.vsa.ecommerce.common.cache.l1.LocalCacheService;
import com.vsa.ecommerce.common.cache.l2.RedisCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for Hybrid Cache Manager.
 * <p>
 * Tests:
 * - L1 → L2 lookup order
 * - L1 warm-up on L2 hit
 * - Cache eviction
 * - getOrCompute functionality
 * <p>
 * Requirements:
 * - Redis instance running on localhost:6379
 * - Spring Boot test context
 */
@SpringBootTest
class HybridCacheIntegrationTest {

    @Autowired
    private HybridCacheService hybridCacheManager;

    @Autowired
    private LocalCacheService l1Cache;

    @Autowired
    private RedisCacheService l2Cache;

    @BeforeEach
    void setUp() {
        // Clear caches before each test
        l1Cache.evictAll();
        // Note: L2 eviction would need pattern-based clearing
    }

    @Test
    void testL1HitAfterCachePut() {
        // Given
        String key = "test:hybrid:user:123";
        TestDto value = new TestDto("John", "Doe");

        // When
        hybridCacheManager.put(key, value);

        // Then - should hit L1
        Optional<TestDto> result = hybridCacheManager.get(key, TestDto.class);
        assertThat(result).isPresent();
        assertThat(result.get().firstName()).isEqualTo("John");
    }

    @Test
    void testL2HitAndL1WarmUp() {
        // Given - put only in L2
        String key = "test:hybrid:user:456";
        TestDto value = new TestDto("Jane", "Smith");
        l2Cache.put(key, value);

        // Ensure L1 doesn't have it
        l1Cache.evict(key);

        // When - get from hybrid cache
        Optional<TestDto> result = hybridCacheManager.get(key, TestDto.class);

        // Then - should hit L2 and warm L1
        assertThat(result).isPresent();
        assertThat(result.get().firstName()).isEqualTo("Jane");

        // Verify L1 is now warmed
        Optional<TestDto> l1Result = l1Cache.get(key, TestDto.class);
        assertThat(l1Result).isPresent();
    }

    @Test
    void testGetOrCompute_CacheMiss() {
        // Given
        String key = "test:hybrid:user:789";
        int[] callCount = { 0 };

        // When
        TestDto result = hybridCacheManager.getOrCompute(
                key,
                TestDto.class,
                () -> {
                    callCount[0]++;
                    return new TestDto("Computed", "User");
                });

        // Then
        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("Computed");
        assertThat(callCount[0]).isEqualTo(1);

        // Second call should hit cache, not compute
        TestDto cachedResult = hybridCacheManager.getOrCompute(
                key,
                TestDto.class,
                () -> {
                    callCount[0]++;
                    return new TestDto("Should Not", "Execute");
                });

        assertThat(cachedResult.firstName()).isEqualTo("Computed");
        assertThat(callCount[0]).isEqualTo(1); // Supplier not called again
    }

    @Test
    void testEviction() {
        // Given
        String key = "test:hybrid:user:999";
        TestDto value = new TestDto("To Be", "Evicted");
        hybridCacheManager.put(key, value);

        // Verify it's cached
        assertThat(hybridCacheManager.get(key, TestDto.class)).isPresent();

        // When
        hybridCacheManager.evict(key);

        // Then - should be evicted from both L1 and L2
        assertThat(hybridCacheManager.get(key, TestDto.class)).isEmpty();
        assertThat(l1Cache.get(key, TestDto.class)).isEmpty();
        assertThat(l2Cache.get(key, TestDto.class)).isEmpty();
    }

    record TestDto(String firstName, String lastName) {
    }
}
