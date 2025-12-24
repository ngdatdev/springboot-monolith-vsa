# Enterprise Caching Architecture

## Table of Contents
1. [Overview](#overview)
2. [Architecture Diagram](#architecture-diagram)
3. [Components](#components)
4. [Cache Types](#cache-types)
5. [When to Use What](#when-to-use-what)
6. [Best Practices](#best-practices)
7. [Anti-Patterns](#anti-patterns)
8. [Multi-Instance Deployment](#multi-instance-deployment)
9. [Performance Metrics](#performance-metrics)
10. [Troubleshooting](#troubleshooting)

---

## Overview

This is a production-grade, multi-layered caching system designed for Spring Boot-based monolithic applications with support for horizontal scaling and future microservices migration.

### Key Features
- **L1 Cache (Caffeine)**: In-memory, sub-millisecond latency, instance-local
- **L2 Cache (Redis)**: Distributed, shared across instances, 5-10ms latency
- **Hybrid Cache**: Intelligent L1+L2 coordination with automatic warm-up
- **Redis Pub/Sub**: Real-time cache invalidation across multiple instances
- **Distributed Locks**: Prevent race conditions in concurrent scenarios
- **Redis Utilities**: Rate limiting, OTP management, feature flags

### Design Principles
1. **Cache-Aside Pattern**: Explicit cache management in service layer
2. **Layered Approach**: Progressive degradation (L1 → L2 → Database)
3. **Fail-Safe Design**: Application continues if cache fails
4. **Observability**: Comprehensive logging and metrics
5. **Scalability**: Supports single to multi-instance deployments

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                      Service Layer                          │
│  (UserService, PermissionService, ConfigService, etc.)      │
└───────────────────┬─────────────────────────────────────────┘
                    │
                    │ Uses
                    ▼
┌─────────────────────────────────────────────────────────────┐
│              HybridCacheManager (Primary Facade)            │
│  • getOrCompute()                                           │
│  • put() / evict()                                          │
│  • Coordinates L1 + L2                                      │
└────┬──────────────────────────────────────────────┬─────────┘
     │                                               │
     │                                               │ Publishes
     │                                               │ invalidation
     ▼                                               ▼
┌──────────────────┐                    ┌────────────────────┐
│   L1 Cache       │                    │  Pub/Sub Publisher │
│   (Caffeine)     │                    │  (Redis Channel)   │
│                  │                    └────────────────────┘
│ • TTL: 30s       │                             │
│ • Max: 1000      │                             │ Broadcasts
│ • Local only     │                             │
└────────┬─────────┘                             ▼
         │                            ┌────────────────────┐
         │ On L1 miss                 │  Pub/Sub Subsciber │
         │                            │  (Other Instances) │
         ▼                            └────────┬───────────┘
┌──────────────────┐                          │
│   L2 Cache       │                          │ Evicts L1
│   (Redis)        │                          │ on events
│                  │◄─────────────────────────┘
│ • TTL: 5min      │
│ • Distributed    │
│ • JSON format    │
└──────────────────┘

┌──────────────────────────────────────────────────────────┐
│               Distributed Lock (Redisson)                │
│  • Prevents race conditions                              │
│  • Cache rebuild coordination                            │
│  • Idempotent operations                                 │
└──────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────┐
│           Redis Beyond Caching                           │
│  • Rate Limiting (sliding window)                        │
│  • OTP/Temporary Tokens (TTL-based)                      │
│  • Feature Flags (hierarchical)                          │
│  • Session Storage                                       │
└──────────────────────────────────────────────────────────┘
```

---

## Components

### 1. Cache Foundation

#### CacheKeyConvention
**Purpose**: Standardized cache key generation  
**Format**: `{namespace}:{version}:{resource}:[{tenantId}]:{identifier}`  
**Examples**:
- `vsa:v1:user::12345`
- `vsa:v1:permission:tenant-abc:read-orders`

**Why Important**:
- Prevents key collisions
- Supports multi-tenancy
- Enables cache versioning
- Simplifies pattern-based operations

#### CacheMetrics
**Purpose**: Track cache performance  
**Metrics**:
- Hits / Misses
- Hit Ratio %
- Evictions
- Put operations

---

### 2. L1 Cache (LocalCacheService)

**Implementation**: `CaffeineLocalCacheService`

**Characteristics**:
| Property | Value |
|----------|-------|
| Latency | <1ms |
| TTL | 30 seconds (configurable) |
| Max Size | 1000 entries (configurable) |
| Eviction | Window TinyLFU |
| Scope | Single instance only |

**When to Use**:
- ✅ Read-heavy data (User profiles, Permissions)
- ✅ Rarely changing data
- ✅ Small datasets (<1000 entries)
- ✅ Sub-millisecond latency required

**When NOT to Use**:
- ❌ Transactional data
- ❌ Frequently updated entities
- ❌ Large datasets
- ❌ Cross-instance consistency required immediately

---

### 3. L2 Cache (RedisCacheService)

**Implementation**: `RedisDistributedCacheService`

**Characteristics**:
| Property | Value |
|----------|-------|
| Latency | 5-10ms |
| TTL | 5 minutes (configurable) |
| Max Size | Limited by Redis memory |
| Serialization | JSON (Jackson) |
| Scope | All instances (shared) |

**When to Use**:
- ✅ Multi-instance deployments
- ✅ Data sharing across services
- ✅ Longer caching duration needed
- ✅ <10ms latency acceptable

**When NOT to Use**:
- ❌ Sub-millisecond latency required
- ❌ Very large objects (>1MB - use object storage)
- ❌ Highly transactional data

---

### 4. Hybrid Cache (HybridCacheManager)

**The Primary Cache Facade for Production**

**Lookup Strategy**:
```java
1. Check L1 (Caffeine) → Return if found ⚡ <1ms
2. Check L2 (Redis) → If found:
   - Warm L1 (store in Caffeine)
   - Return value ⚡ 5-10ms
3. Return empty if both miss → Database call needed ⚡ 50-200ms
```

**Key Methods**:

```java
// Get from cache or compute if missing (Cache-Aside)
T getOrCompute(String key, Class<T> type, Supplier<T> supplier)

// Explicit put
void put(String key, T value)

// Evict with Pub/Sub notification
void evict(String key)
void evictPattern(String pattern)
```

**Performance Benefits**:
- 95%+ L1 hit ratio → <1ms latency
- 3-5% L2 hit ratio → 5-10ms latency
- <1% database hit → 50-200ms latency

---

### 5. Redis Pub/Sub Cache Invalidation

**Problem Solved**: In multi-instance deployments, L1 cache is not shared. When Instance A updates data and evicts cache, Instance B's L1 still has stale data.

**Solution**: Pub/Sub invalidation events

**Flow**:
```
Instance A: Update user → Evict L1 & L2 → Publish "invalidate:user:123"
   ↓
Redis Channel: "cache-invalidation"
   ↓
Instance B, C, D: Receive event → Evict L1 only (L2 already updated)
   ↓
All instances: Consistent state
```

**Event Types**:
- `SINGLE_KEY`: Invalidate specific key (`vsa:v1:user::123`)
- `PATTERN`: Invalidate pattern (`vsa:v1:permission:*`)

---

### 6. Distributed Lock (DistributedLockService)

**Implementation**: `RedissonDistributedLockService` (Redisson-based)

**Use Cases**:
1. **Cache Rebuild**: Ensure only one instance rebuilds expensive cache
2. **Idempotent Requests**: Prevent duplicate processing
3. **Order Processing**: Prevent double-debit
4. **Scheduled Jobs**: Single execution across cluster

**Example Usage**:

```java
// Prevent thundering herd on cache rebuild
Optional<ConfigMap> config = lockService.executeWithLock(
    "lock:cache:rebuild:config",
    () -> loadExpensiveConfigFromDatabase()
);
```

**Features**:
- Automatic lock renewal
- Timeout protection  
- Automatic release on exception
- Fair locking (FIFO)

---

## Cache Types

### Comparison Table

| Type | Latency | TTL | Shared | Use Case | Example |
|------|---------|-----|--------|----------|---------|
| **L1 Only** | <1ms | 30s | ❌ No | Very read-heavy, single instance | NA |
| **L2 Only** | 5-10ms | 5min | ✅ Yes | Multi-instance, moderate reads | Session data |
| **Hybrid (L1+L2)** | <1ms (hot) / 5-10ms (warm) | 30s/5min | ✅ Yes | Production default | User profiles, Permissions |

---

## When to Use What

### ✅ Use **HybridCacheManager** (Recommended for most cases)

**Good for**:
- User profiles
- Permissions / Roles
- Application configuration
- Product catalogs (read-heavy)
- Feature flags
- Multi-instance deployments

**Example**:
```java
@Service
public class UserService {
    private final HybridCacheManager cache;
    
    public UserDto getUser(Long id) {
        String key = keyConvention.buildKey("user", id.toString());
        return cache.getOrCompute(key, UserDto.class, 
            () -> userRepository.findById(id).map(UserMapper::toDto));
    }
}
```

---

### ✅ Use **L1 Only** (LocalCacheService)

**Good for**:
- Single instance deployments
- Process-specific caching
- Temporary computation results

**Example**:
```java
// Cache expensive calculation results temporarily
String key = "calculation:" + inputHash;
Optional<ResultDto> cached = l1Cache.get(key, ResultDto.class);
```

---

### ✅ Use **L2 Only** (RedisCacheService)

**Good for**:
- Session storage
- Shared state across services
- When L1's short TTL is problematic

---

### ❌ **DO NOT Cache**

❌ **In Controllers**
```java
// WRONG
@GetMapping("/users/{id}")
public UserDto getUser(@PathVariable Long id) {
    // NO caching logic here
}
```

❌ **During Active Transactions**
```java
// WRONG
@Transactional
public void updateUser(User user) {
    userRepository.save(user);
    cache.put(key, user); // ❌ Cache inside transaction
}
```

❌ **JPA Entities Directly**
```java
// WRONG
User entity = userRepository.findById(id);
cache.put(key, entity); // ❌ JPA entity has lazy loading issues
```

✅ **Cache DTOs instead**
```java
// CORRECT
UserDto dto = UserMapper.toDto(entity);
cache.put(key, dto); // ✅ Immutable DTO
```

---

## Best Practices

### 1. **Always Use DTOs, Never JPA Entities**

❌ **Bad**:
```java
@Entity
public class User {
    @OneToMany(fetch = FetchType.LAZY)
    private List<Order> orders; // Lazy loading breaks serialization
}

cache.put(key, userEntity); // ❌ Will fail on deserialization
```

✅ **Good**:
```java
public record UserDto(Long id, String email, String name) {}

cache.put(key, new UserDto(...)); // ✅ Immutable, serializable
```

---

### 2. **L1 TTL < L2 TTL (Critical)**

⚠️ **Why Important**: If L1 TTL > L2 TTL, L1 may hold stale data after L2 expires.

✅ **Configuration**:
```yaml
cache:
  caffeine:
    ttl-seconds: 30  # L1: 30 seconds
  redis:
    ttl-minutes: 5   # L2: 5 minutes (300 seconds)
```

---

### 3. **Invalidate Cache on Every Write**

✅ **Pattern**:
```java
@Transactional
public void updateUser(Long id, UserDto dto) {
    // 1. Update database
    userRepository.update(id, dto);
}

// 2. Invalidate cache AFTER transaction commits
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void onUserUpdated(UserUpdatedEvent event) {
    String key = keyConvention.buildKey("user", event.userId().toString());
    hybridCacheManager.evict(key);
}
```

---

### 4. **Use Distributed Locks for Expensive Operations**

✅ **Example: Cache Warm-up**
```java
public void warmUpCache() {
    lockService.executeWithLock("lock:warmup:users", () -> {
        List<UserDto> users = loadAllUsersFromDatabase();
        users.forEach(user -> {
            String key = keyConvention.buildKey("user", user.id().toString());
            hybridCacheManager.put(key, user);
        });
    });
}
```

---

### 5. **Log Cache Hit/Miss for Monitoring**

✅ Already built-in:
```java
log.trace("L1 Cache HIT: {}", key);   // Caffeine hit
log.debug("L2 Cache HIT: {}", key);   // Redis hit
log.debug("Cache MISS: {}", key);     // Both missed
```

---

## Anti-Patterns

### ❌ 1. Caching Inside Transactions

```java
@Transactional
public void processOrder(Order order) {
    orderRepository.save(order);
    cache.put(key, order); // ❌ Cache update before transaction commits
    // If transaction rolls back, cache has invalid data!
}
```

---

### ❌ 2. Not Invalidating Related Caches

```java
public void updateUserRole(Long userId, String newRole) {
    userRepository.updateRole(userId, newRole);
    cache.evict("user:" + userId); // ✅ User cache invalidated
    // ❌ Forgot to invalidate permissions cache!
}
```

✅ **Solution**:
```java
cache.evict("user:" + userId);
cache.evictPattern("permission:*:" + userId); // ✅ Invalidate related data
```

---

### ❌ 3. Over-Caching

Don't cache everything:
- Transactional data (orders being processed)
- Highly mutable data (stock levels in high-frequency trading)
- Large binary objects (use object storage instead)

---

## Multi-Instance Deployment

### Single Instance
```text
✅ L1 + L2 work perfectly
✅ Pub/Sub optional (no other instances to notify)
✅ Distributed locks useful for scheduled jobs
```

### Multi-Instance (Recommended)
```text
✅ L1 + L2 + Pub/Sub required
✅ Cache invalidation across instances
✅ Distributed locks required for critical sections
✅ Horizontal scaling supported
```

### Configuration (application.yml)
```yaml
cache:
  default-namespace: vsa
  default-version: v1
  caffeine:
    max-size: 1000
    ttl-seconds: 30
  redis:
    ttl-minutes: 5
  pub-sub:
    channel: cache-invalidation  # All instances listen here
  locks:
    wait-time-seconds: 10
    lease-time-seconds: 30
```

---

## Performance Metrics

### Expected Hit Ratios (Production)

| Cache Level | Hit Ratio | Latency | Traffic % |
|------------|-----------|---------|-----------|
| L1 (Caffeine) | 90-95% | <1ms | 90-95% |
| L2 (Redis) | 3-5% | 5-10ms | 3-5% |
| Database | 1-5% | 50-200ms | 1-5% |

### Monitoring

**Spring Boot Actuator Metrics**:
```bash
# Cache hits/misses
GET /actuator/metrics/cache.gets?tag=name:hybridCache

# Redis operations
GET /actuator/metrics/redis.operations
```

**Redis CLI Monitoring**:
```bash
redis-cli INFO stats
redis-cli MONITOR         # Real-time command monitoring
redis-cli KEYS "vsa:*"    # List all cache keys
```

---

## Troubleshooting

### Issue: Stale Data in L1 Cache

**Symptoms**: User updates data, but L1 on other instances still shows old data

**Solution**: Verify Pub/Sub is configured
```bash
# Check if Pub/Sub subscriber is running
redis-cli SUBSCRIBE cache-invalidation

# Trigger cache eviction and watch for message
# Should see: {"key":"vsa:v1:user::123","type":"SINGLE_KEY",...}
```

---

### Issue: High Cache Miss Rate

**Symptoms**: Cache hit ratio < 70%

**Diagnosis**:
```java
// Check cache statistics
CacheStats stats = ((CaffeineLocalCacheService) l1Cache).getStats();
log.info("L1 Hit Ratio: {}%", stats.hitRate() * 100);
```

**Solutions**:
- Increase L1 max size
- Increase TTL if data doesn't change frequently
- Pre-warm cache on startup

---

### Issue: Distributed Lock Timeout

**Symptoms**: `executeWithLock` returns empty

**Solution**: Increase wait time or lease time
```yaml
cache:
  locks:
    wait-time-seconds: 30   # Increase if operations take longer
    lease-time-seconds: 60
```

---

## Migration Path to Microservices

This caching architecture is **microservice-ready**:

1. **Each service** gets its own cache namespace:
   ```java
   // Order Service
   cache.default-namespace: order-service
   
   // User Service
   cache.default-namespace: user-service
   ```

2. **Shared Redis cluster** across all services

3. **Service-specific Pub/Sub channels**:
   ```yaml
   cache.pub-sub.channel: cache-invalidation-order-service
   ```

4. **No code changes** required - just configuration updates!

---

## Summary

✅ **Use HybridCacheManager** as your primary cache facade  
✅ **L1 TTL < L2 TTL** always  
✅ **Cache DTOs**, never JPA entities  
✅ **Invalidate on every write**  
✅ **Use distributed locks** for expensive operations  
✅ **Monitor cache hit ratios** via actuator metrics  
✅ **Pub/Sub required** for multi-instance deployments

**Result**: Production-grade caching system with 90%+ hit ratio, <1ms latency for 95% of requests, and full multi-instance support.
