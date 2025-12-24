# Cache Invalidation Strategies

## Overview

Cache invalidation is one of the hardest problems in computer science. This document compares different strategies and provides guidance on when to use each.

---

## Strategy Comparison

| Strategy | Complexity | Consistency | Scalability | Best For |
|----------|-----------|-------------|-------------|----------|
| **TTL Only** | ⭐ Low | ⚠️ Eventual | ✅ Excellent | Read-heavy, rarely changing data |
| **Evict-on-Write** | ⭐⭐ Medium | ✅ Strong | ✅ Good | Frequently updated data |
| **Pub/Sub** | ⭐⭐⭐ High | ✅ Strong | ✅ Excellent | Multi-instance deployments |
| **Versioned Keys** | ⭐⭐ Medium | ✅ Strong | ✅ Excellent | Breaking changes, rollbacks |

---

## 1. TTL-Only Strategy

### How It Works
Cache entries automatically expire after a fixed time period. No explicit invalidation.

### Pros
✅ Simple to implement  
✅ No invalidation logic needed  
✅ Predictable memory usage  
✅ Works well with CDNs

### Cons
❌ Stale data possible for full TTL duration  
❌ Not suitable for frequently updated data  
❌ No control over invalidation timing

### When to Use
- Read-heavy data that rarely changes
- Acceptable staleness window (e.g., product catalogs)
- Single instance deployments
- CDN caching

### Example

```java
// User profile changes infrequently
@Value("${cache.user-profile-ttl-minutes:10}")
private int userProfileTtl;

public UserDto getUserProfile(Long userId) {
    String key = buildKey("user-profile", userId);
    return cache.getOrCompute(key, UserDto.class, () -> {
        // Will be cached for 10 minutes
        return userRepository.findById(userId);
    });
}
```

### Configuration
```yaml
cache:
  caffeine:
    ttl-seconds: 300  # 5 minutes for rarely changing data
  redis:
    ttl-minutes: 30   # 30 minutes for very stable data
```

---

## 2. Evict-on-Write Strategy

### How It Works
Explicitly evict cache entries whenever the underlying data is updated or deleted.

### Pros
✅ Strong consistency  
✅ Immediate invalidation  
✅ Predictable behavior  
✅ Simple to reason about

### Cons
❌ Requires invalidation logic in every write operation  
❌ Can miss invalidations if not implemented carefully  
❌ More complex in multi-tenant scenarios

### When to Use
- Frequently updated data
- Strong consistency required
- Single or multi-instance (with Pub/Sub)
- Transactional updates

### Example

```java
@Service
public class UserService {
    
    @Transactional
    public void updateUser(Long userId, UserDto dto) {
        // Update database
        userRepository.update(userId, dto);
    }
    
    // Invalidate AFTER transaction commits
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserUpdated(UserUpdatedEvent event) {
        String key = buildKey("user", event.getUserId());
        cache.evict(key);
        log.info("Cache invalidated for user: {}", event.getUserId());
    }
}
```

### Important Rules
⚠️ **Never** evict inside `@Transactional` method  
⚠️ **Always** evict after transaction commits  
⚠️ **Don't forget** to evict related caches

---

## 3. Pub/Sub Strategy (Multi-Instance)

### How It Works
When one instance updates data, it publishes an invalidation event via Redis Pub/Sub. All other instances receive the event and evict their local L1 caches.

### Pros
✅ Works across multiple instances  
✅ Real-time invalidation  
✅ Strong consistency across cluster  
✅ Scalable horizontally

### Cons
❌ Higher complexity  
❌ Requires Redis infrastructure  
❌ Small network overhead  
❌ Pub/Sub delivery not guaranteed (fire-and-forget)

### When to Use
- **REQUIRED** for multi-instance deployments
- Horizontal scaling needed
- Microservices architecture
- Cloud deployments (multiple pods/containers)

### Architecture

```
Instance A                    Instance B, C, D
   │                               │
   │ 1. Update DB                  │
   │ 2. Evict L1 & L2              │
   │ 3. Publish event              │
   │                               │
   └──────► Redis Pub/Sub ────────►│
            Channel: "cache-       │ 4. Receive event
            invalidation"           │ 5. Evict L1 only
                                    │    (L2 already updated)
```

### Example

```java
// Instance A updates and publishes
public void updateUser(Long userId, UserDto dto) {
    userRepository.update(userId, dto);
    
    String key = buildKey("user", userId);
    
    // Evict from both L1 and L2
    hybridCacheManager.evict(key);
    
    // This internally publishes: {"key":"vsa:v1:user::123","type":"SINGLE_KEY"}
}

// Instances B, C, D receive and evict L1
@Component
public class CacheInvalidationSubscriber {
    public void handleMessage(String message) {
        CacheInvalidationEvent event = parse(message);
        if (event.type() == SINGLE_KEY) {
            l1Cache.evict(event.key());
        }
    }
}
```

### Configuration
```yaml
cache:
  pub-sub:
    channel: cache-invalidation  # All instances subscribe to this channel
```

### Comparison: TTL Only vs TTL + Pub/Sub

| Scenario | TTL Only | TTL + Pub/Sub |
|----------|----------|---------------|
| Update on Instance A | Stale for 30s on B, C, D | Invalidated within <50ms |
| Consistency Window | 30 seconds | <50 milliseconds |
| Code Complexity | Low | Medium |
| Infrastructure | None extra | Redis required |

**Recommendation**: Use Pub/Sub for all multi-instance deployments.

---

## 4. Versioned Keys Strategy

### How It Works
Append a version number to cache keys. When schema/format changes, increment version. Old cached data is ignored.

### Pros
✅ Zero-downtime deployments  
✅ Support for rollbacks  
✅ No need to flush entire cache  
✅ Gradual migration possible

### Cons
❌ Old cached data remains until TTL expires  
❌ Wastes memory temporarily  
❌ Requires coordination during deployment

### When to Use
- Breaking changes in data structure
- Zero-downtime deployments
- A/B testing with different data formats
- Blue-green deployments

### Example

```java
// v1 format
public record UserDtoV1(Long id, String email) {}

// v2 format (added field)
public record UserDtoV2(Long id, String email, String phone) {}

// Old deployment (v1)
String keyV1 = "vsa:v1:user::123";  // Uses UserDtoV1
cache.put(keyV1, new UserDtoV1(...));

// New deployment (v2)
String keyV2 = "vsa:v2:user::123";  // Uses UserDtoV2
cache.put(keyV2, new UserDtoV2(...));

// keyV1 ignored, will expire via TTL
```

### Implementation
```java
@Value("${cache.default-version:v1}")
private String cacheVersion;

public String buildKey(String resource, String id) {
    return String.format("%s:%s:%s::%s", 
        namespace, 
        cacheVersion,  // ← Version here
        resource, 
        id);
}
```

### Configuration
```yaml
cache:
  default-version: v2  # Bump this on breaking changes
```

---

## Decision Matrix

### Choose Strategy Based on Requirements

```
┌─────────────────────────────────────────────────────────┐
│                  Decision Tree                          │
└─────────────────────────────────────────────────────────┘

Is data read-heavy and rarely changing?
├─ YES → TTL Only ✅
│
└─ NO → Is data frequently updated?
    ├─ YES → Evict-on-Write ✅
    │
    └─ NO → Multi-instance deployment?
        ├─ YES → Evict-on-Write + Pub/Sub ✅ (REQUIRED)
        │
        └─ NO → Evict-on-Write ✅

Breaking changes in data format?
└─ YES → Versioned Keys ✅ (in addition to above)
```

---

## Hybrid Approach (Recommended)

**Combine multiple strategies for best results**:

```java
@Service
public class ProductService {
    
    // Strategy 1: TTL (5 minutes)
    // Products don't change frequently
    public ProductDto getProduct(Long id) {
        String key = buildKey("product", id);
        return cache.getOrCompute(key, ProductDto.class, 
            () -> productRepository.findById(id));
    }
    
    // Strategy 2: Evict-on-Write + Pub/Sub
    // Explicit invalidation when product updated
    public void updateProduct(Long id, ProductDto dto) {
        productRepository.update(id, dto);
        
        String key = buildKey("product", id);
        cache.evict(key);  // Triggers Pub/Sub internally
    }
    
    // Strategy 3: Versioned Keys (for format changes)
    // Version bumped when ProductDto schema changes
    private String buildKey(String resource, Long id) {
        return keyConvention.buildKey(resource, id.toString());
        // Format: "vsa:v2:product::12345"
        //                 ^^
        //                 Version from config
    }
}
```

**Result**:
- Products cache for 5 minutes (TTL)
- Update immediately invalidates cache (Evict-on-Write)
- Invalidation works across instances (Pub/Sub)
- Can deploy breaking changes safely (Versioned Keys)

---

## Best Practices Summary

### ✅ DO
- Use **TTL** as baseline for all caches
- Add **Evict-on-Write** for strong consistency
- Enable **Pub/Sub** for multi-instance deployments
- Use **Versioned Keys** for breaking changes
- Combine strategies as needed

### ❌ DON'T
- Rely on TTL alone for critical data
- Forget to evict on updates
- Skip Pub/Sub in multi-instance setups
- Use infinite TTLs (memory leak risk)

---

## Real-World Examples

### Example 1: User Profile (Read-Heavy)

**Requirements**:
- Read 1000x more than writes
- Acceptable 30s staleness
- Multi-instance deployment

**Strategy**:
```java
// TTL: 30s + Evict-on-Write + Pub/Sub
cache.getOrCompute(key, UserDto.class, supplier);  // 30s TTL
cache.evict(key);  // On update (with Pub/Sub)
```

**Result**: 95% L1 hit ratio, <1ms latency, strong consistency on updates

---

### Example 2: Product Inventory (Frequently Updated)

**Requirements**:
- Updated every few seconds
- Strong consistency required
- Multi-instance deployment

**Strategy**:
```java
// TTL: 10s + Evict-on-Write + Pub/Sub
// Short TTL as fallback, but rely on eviction
cache.getOrCompute(key, InventoryDto.class, supplier);
cache.evict(key);  // On every stock change
```

**Result**: Strong consistency, 10s fallback if eviction delayed

---

### Example 3: Application Config (Rarely Changes)

**Requirements**:
- Changes once per month
- Can tolerate 5min staleness
- Multi-instance deployment

**Strategy**:
```java
// TTL: 5min + Evict-on-Write (optional) + Versioned Keys
cache.getOrCompute(key, ConfigDto.class, supplier);
// Manual eviction if needed, otherwise TTL handles it
```

**Result**: Very high hit ratio, minimal invalidation overhead

---

## Monitoring Invalidation

### Track Invalidation Events

```java
@Component
public class CacheInvalidationMetrics {
    private final AtomicLong evictionCount = new AtomicLong(0);
    
    public void recordEviction() {
        long count = evictionCount.incrementAndGet();
        if (count % 100 == 0) {
            log.info("Cache evictions: {}", count);
        }
    }
}
```

### Redis Pub/Sub Monitoring

```bash
# Monitor all Pub/Sub messages
redis-cli SUBSCRIBE cache-invalidation

# Expected output on cache eviction:
# {"key":"vsa:v1:user::123","type":"SINGLE_KEY","timestamp":1672531200000}
```

---

## Conclusion

**For Production Deployments**:

| Deployment Type | Recommended Strategy |
|----------------|---------------------|
| **Single Instance** | TTL + Evict-on-Write |
| **Multi-Instance** | TTL + Evict-on-Write + **Pub/Sub** (required) |
| **With Breaking Changes** | Add Versioned Keys |

**Golden Rule**: Start simple (TTL only), add complexity as needed (Evict-on-Write → Pub/Sub → Versioned Keys).
