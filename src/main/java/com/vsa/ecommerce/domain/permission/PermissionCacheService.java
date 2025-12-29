package com.vsa.ecommerce.domain.permission;

import com.vsa.ecommerce.common.cache.CacheKeyConvention;
import com.vsa.ecommerce.common.cache.hybrid.HybridCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.vsa.ecommerce.common.cache.CacheKeyConvention.RESOURCE_PERMISSION;

/**
 * Example service demonstrating multi-tenant permission caching.
 * <p>
 * Caching Strategy:
 * - Tenant-aware cache keys
 * - Pattern-based invalidation
 * - Cache entire permission sets per user
 * <p>
 * Use Case:
 * - Authorization checks (called on every request)
 * - Multi-tenant permission management
 * - Role-based access control (RBAC)
 * <p>
 * Special Considerations:
 * - When role permissions change, invalidate all users in that role
 * - Use pattern eviction: "vsa:v1:permission:tenant-123:*"
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionCacheService {

    private final HybridCacheService cacheManager;
    private final CacheKeyConvention keyConvention;
    // Inject your PermissionRepository here
    // private final PermissionRepository permissionRepository;

    /**
     * Get user permissions with tenant context.
     * Example of tenant-aware caching.
     */
    public Set<String> getUserPermissions(String tenantId, Long userId) {
        if (tenantId == null || userId == null) {
            return Set.of();
        }

        String cacheKey = keyConvention.buildKey(RESOURCE_PERMISSION, userId.toString(), tenantId);

        return cacheManager.getOrCompute(
                cacheKey,
                PermissionSet.class,
                () -> loadPermissionsFromDatabase(tenantId, userId)).permissions();
    }

    /**
     * Check if user has a specific permission.
     */
    public boolean hasPermission(String tenantId, Long userId, String permission) {
        Set<String> permissions = getUserPermissions(tenantId, userId);
        return permissions.contains(permission);
    }

    /**
     * Invalidate permissions for a specific user.
     * Called when user roles change.
     */
    public void invalidateUserPermissions(String tenantId, Long userId) {
        String cacheKey = keyConvention.buildKey(RESOURCE_PERMISSION, userId.toString(), tenantId);
        cacheManager.evict(cacheKey);
        log.info("Invalidated permissions for user {} in tenant {}", userId, tenantId);
    }

    /**
     * Invalidate all permissions in a tenant.
     * Called when role definitions change.
     * <p>
     * Uses pattern-based invalidation.
     */
    public void invalidateAllPermissionsInTenant(String tenantId) {
        String pattern = keyConvention.buildTenantPattern(RESOURCE_PERMISSION, tenantId);
        cacheManager.evictPattern(pattern);
        log.info("Invalidated all permissions in tenant: {}", tenantId);
    }

    /**
     * Simulate loading permissions from database.
     */
    private PermissionSet loadPermissionsFromDatabase(String tenantId, Long userId) {
        log.debug("Loading permissions from database for user {} in tenant {}", userId, tenantId);

        // TODO: Replace with actual repository call
        // return permissionRepository.findByTenantAndUser(tenantId, userId)
        // .map(PermissionMapper::toDto)
        // .orElse(new PermissionSet(Set.of()));

        // Mock data
        return new PermissionSet(Set.of(
                "read:orders",
                "write:orders",
                "read:products"));
    }

    /**
     * DTO for permission data.
     * Wrapping Set in a record for Jackson serialization compatibility.
     */
    public record PermissionSet(Set<String> permissions) {
    }
}
