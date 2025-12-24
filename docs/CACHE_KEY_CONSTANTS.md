# Cache Key Constants Reference Guide

## Overview

All cache key resource names are now centralized in `CacheKeyConvention` as public static constants for type-safety and consistency.

## Available Constants

### Core Business Entities

```java
// User entities
CacheKeyConvention.RESOURCE_USER = "user"
// Example: vsa:v1:user::12345

// Order management
CacheKeyConvention.RESOURCE_ORDER = "order"
// Example: vsa:v1:order::98765

CacheKeyConvention.RESOURCE_ORDER_ITEM = "order-item"
// Example: vsa:v1:order-item::11223

// Product catalog
CacheKeyConvention.RESOURCE_PRODUCT = "product"
// Example: vsa:v1:product::SKU-123

// Payment processing
CacheKeyConvention.RESOURCE_PAYMENT = "payment"
// Example: vsa:v1:payment::txn-456
```

### Inventory Management

```java
CacheKeyConvention.RESOURCE_INVENTORY = "inventory"
// Example: vsa:v1:inventory::warehouse-A

CacheKeyConvention.RESOURCE_INVENTORY_TRANSACTION = "inventory-transaction"
// Example: vsa:v1:inventory-transaction::txn-789
```

### System Resources

```java
CacheKeyConvention.RESOURCE_NOTIFICATION = "notification"
// Example: vsa:v1:notification::notif-123

CacheKeyConvention.RESOURCE_PERMISSION = "permission"
// Example: vsa:v1:permission:tenant-abc:read-orders

CacheKeyConvention.RESOURCE_CONFIG = "config"
// Example: vsa:v1:config::feature-flags
```

### Composite Keys (Complex Queries)

```java
// Cache all orders for a specific user
CacheKeyConvention.RESOURCE_USER_ORDERS = "user-orders"
// Example: vsa:v1:user-orders::user-123

// Cache inventory status for a product
CacheKeyConvention.RESOURCE_PRODUCT_INVENTORY = "product-inventory"
// Example: vsa:v1:product-inventory::product-456

// Cache order summary data
CacheKeyConvention.RESOURCE_ORDER_SUMMARY = "order-summary"
// Example: vsa:v1:order-summary::monthly-2024-12
```

### Temporary Resources

```java
CacheKeyConvention.RESOURCE_SESSION = "session"
// Example: vsa:v1:session::sess-abc123

CacheKeyConvention.RESOURCE_OTP = "otp"
// Example: vsa:v1:otp::user@example.com

CacheKeyConvention.RESOURCE_FEATURE_FLAG = "feature-flag"
// Example: vsa:v1:feature-flag:global:new-checkout

CacheKeyConvention.RESOURCE_RATE_LIMIT = "rate-limit"
// Example: vsa:v1:rate-limit::user-123
```

## Usage Examples

### Example 1: User Profile Caching

```java
import static com.vsa.ecommerce.common.cache.CacheKeyConvention.*;

@Service
public class UserService {
    private final HybridCacheManager cache;
    private final CacheKeyConvention keyConvention;
    
    public UserDto getUser(Long userId) {
        // Use constant instead of hardcoded string
        String key = keyConvention.buildKey(RESOURCE_USER, userId.toString());
        
        return cache.getOrCompute(key, UserDto.class, 
            () -> loadUserFromDatabase(userId));
    }
    
    public void updateUser(Long userId, UserDto dto) {
        // Update database
        userRepository.update(userId, dto);
        
        // Invalidate using constant
        String key = keyConvention.buildKey(RESOURCE_USER, userId.toString());
        cache.evict(key);
    }
}
```

### Example 2: Product Inventory Caching

```java
import static com.vsa.ecommerce.common.cache.CacheKeyConvention.*;

@Service
public class InventoryService {
    
    public InventoryDto getProductInventory(Long productId) {
        String key = keyConvention.buildKey(
            RESOURCE_PRODUCT_INVENTORY, 
            productId.toString()
        );
        
        return cache.getOrCompute(key, InventoryDto.class,
            () -> calculateInventory(productId));
    }
    
    public void updateInventory(Long productId, int quantity) {
        // Update inventory
        inventoryRepository.adjustStock(productId, quantity);
        
        // Invalidate both product and inventory caches
        cache.evict(keyConvention.buildKey(RESOURCE_PRODUCT, productId.toString()));
        cache.evict(keyConvention.buildKey(RESOURCE_PRODUCT_INVENTORY, productId.toString()));
    }
}
```

### Example 3: User's Orders (Composite Key)

```java
import static com.vsa.ecommerce.common.cache.CacheKeyConvention.*;

@Service
public class OrderService {
    
    public List<OrderDto> getUserOrders(Long userId) {
        // Use composite key for list of orders
        String key = keyConvention.buildKey(
            RESOURCE_USER_ORDERS,
            userId.toString()
        );
        
        return cache.getOrCompute(key, OrderListDto.class,
            () -> orderRepository.findByUserId(userId))
            .getOrders();
    }
    
    public void createOrder(Long userId, OrderDto order) {
        orderRepository.save(order);
        
        // Invalidate user's order list
        String userOrdersKey = keyConvention.buildKey(
            RESOURCE_USER_ORDERS, 
            userId.toString()
        );
        cache.evict(userOrdersKey);
    }
}
```

### Example 4: Multi-Tenant Permission Cache

```java
import static com.vsa.ecommerce.common.cache.CacheKeyConvention.*;

@Service
public class PermissionService {
    
    public Set<String> getUserPermissions(String tenantId, Long userId) {
        // Build key with tenant context
        String key = keyConvention.buildKey(
            RESOURCE_PERMISSION,
            userId.toString(),
            tenantId  // Tenant ID for multi-tenancy
        );
        
        return cache.getOrCompute(key, PermissionSet.class,
            () -> loadPermissions(tenantId, userId))
            .permissions();
    }
    
    public void invalidateAllPermissionsForTenant(String tenantId) {
        // Use pattern to invalidate all permissions in tenant
        String pattern = keyConvention.buildTenantPattern(
            RESOURCE_PERMISSION, 
            tenantId
        );
        cache.evictPattern(pattern);
    }
}
```

### Example 5: Order Summary Report

```java
import static com.vsa.ecommerce.common.cache.CacheKeyConvention.*;

@Service
public class ReportingService {
    
    public OrderSummaryDto getMonthlyOrderSummary(String month) {
        String key = keyConvention.buildKey(
            RESOURCE_ORDER_SUMMARY,
            "monthly-" + month  // e.g., "monthly-2024-12"
        );
        
        return cache.getOrCompute(key, OrderSummaryDto.class,
            () -> calculateMonthlyOrders(month));
    }
}
```

## Migration from Hardcoded Strings

**Before (Not Recommended)**:
```java
String key = keyConvention.buildKey("user", userId.toString());  // ❌ Magic string
```

**After (Recommended)**:
```java
import static com.vsa.ecommerce.common.cache.CacheKeyConvention.*;

String key = keyConvention.buildKey(RESOURCE_USER, userId.toString());  // ✅ Type-safe constant
```

## Benefits of Using Constants

1. ✅ **Type Safety**: IDE autocomplete and compile-time checking
2. ✅ **Consistency**: Same resource name across entire codebase
3. ✅ **Refactoring**: Easy to find and replace all usages
4. ✅ **Documentation**: Clear definition of all cache resources
5. ✅ **No Typos**: Constants eliminate string typo errors

## Pattern Matching Examples

```java
// Evict all users
String pattern = keyConvention.buildPattern(RESOURCE_USER);
cache.evictPattern(pattern);
// Pattern: vsa:v1:user:*

// Evict all permissions for tenant
String pattern = keyConvention.buildTenantPattern(RESOURCE_PERMISSION, "tenant-123");
cache.evictPattern(pattern);
// Pattern: vsa:v1:permission:tenant-123:*
```

## Adding New Constants

When adding new entities or cache resources:

1. Add constant to `CacheKeyConvention`:
```java
public static final String RESOURCE_MY_ENTITY = "my-entity";
```

2. Use in services:
```java
import static com.vsa.ecommerce.common.cache.CacheKeyConvention.*;

String key = keyConvention.buildKey(RESOURCE_MY_ENTITY, id.toString());
```

3. Document in this reference guide

## Summary

All cache key constants are now centralized in `CacheKeyConvention.java`. Always use these constants instead of hardcoded strings for:
- Type safety
- Consistency
- Easier refactoring
- Better documentation
- Fewer bugs
