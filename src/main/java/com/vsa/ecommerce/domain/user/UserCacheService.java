package com.vsa.ecommerce.domain.user;

import com.vsa.ecommerce.common.cache.CacheKeyConvention;
import com.vsa.ecommerce.common.cache.hybrid.HybridCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.vsa.ecommerce.common.cache.CacheKeyConvention.RESOURCE_USER;

/**
 * Example service demonstrating proper cache usage for User entities.
 * <p>
 * Caching Strategy:
 * - Uses HybridCacheManager (L1 + L2)
 * - L1 TTL: 30 seconds
 * - L2 TTL: 5 minutes
 * - Cache-aside pattern
 * - Invalidate on update/delete
 * <p>
 * Use Case:
 * - Read-heavy user profile data
 * - Reduce database load for authentication/authorization
 * <p>
 * IMPORTANT:
 * - DO NOT cache JPA entities directly
 * - Cache DTOs or immutable snapshots instead
 * - Invalidate cache on every write operation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserCacheService {

    private final HybridCacheService cacheManager;
    private final CacheKeyConvention keyConvention;
    // Inject your UserRepository here
    // private final UserRepository userRepository;

    /**
     * Get user by ID with caching.
     * Example of Cache-Aside pattern.
     */
    public Optional<UserDto> getUserById(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }

        String cacheKey = keyConvention.buildKey(RESOURCE_USER, userId.toString());

        // Try to get from cache (L1 → L2 → empty)
        return Optional.ofNullable(
                cacheManager.getOrCompute(
                        cacheKey,
                        UserDto.class,
                        () -> loadUserFromDatabase(userId)));
    }

    /**
     * Update user and invalidate cache.
     * ALWAYS invalidate cache after write operations.
     */
    public void updateUser(Long userId, UserDto userDto) {
        if (userId == null || userDto == null) {
            return;
        }

        // 1. Update database
        // userRepository.update(userId, userDto);

        // 2. Invalidate cache
        String cacheKey = keyConvention.buildKey(RESOURCE_USER, userId.toString());
        cacheManager.evict(cacheKey);

        log.info("User updated and cache invalidated: {}", userId);
    }

    /**
     * Delete user and invalidate cache.
     */
    public void deleteUser(Long userId) {
        if (userId == null) {
            return;
        }

        // 1. Delete from database
        // userRepository.delete(userId);

        // 2. Invalidate cache
        String cacheKey = keyConvention.buildKey(RESOURCE_USER, userId.toString());
        cacheManager.evict(cacheKey);

        log.info("User deleted and cache invalidated: {}", userId);
    }

    /**
     * Simulate loading user from database.
     * Replace with actual repository call.
     */
    private UserDto loadUserFromDatabase(Long userId) {
        log.debug("Loading user from database (cache miss): {}", userId);

        // TODO: Replace with actual repository call
        // return userRepository.findById(userId)
        // .map(UserMapper::toDto)
        // .orElse(null);

        // Mock data for demonstration
        return new UserDto(userId, "user" + userId + "@example.com", "User " + userId);
    }

    /**
     * Example DTO for user data.
     * Use DTOs instead of JPA entities for caching.
     */
    public record UserDto(
            Long id,
            String email,
            String name) {
    }
}
