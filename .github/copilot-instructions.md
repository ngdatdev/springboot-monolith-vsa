# GitHub Copilot Instructions - Java VSA Monolith Project

## 📋 Project Overview

This is a **Spring Boot E-commerce** project following **Vertical Slice Architecture (VSA)**.
Each API feature is **completely self-contained** with its own Controller, Service, Repository, Request, and Response.

**🚫 NO SHARED DTOs** - Each feature must have its own Response class.

---

## 🏗️ Vertical Slice Architecture Structure

### Standard Feature Structure
Each API feature should be organized in its own folder under `feature/{domain}/{feature_name}/`:

```
feature/
└── {domain}/                          # e.g., product, order, auth, user, cart
    └── {feature_name}/                # e.g., create_product, list_products
        ├── {FeatureName}Controller.java
        ├── {FeatureName}Service.java
        ├── {FeatureName}Repository.java
        ├── {FeatureName}Request.java
        ├── {FeatureName}Response.java   # REQUIRED - each feature has its own Response
        └── {ItemName}.java              # Optional - for list responses (e.g., ProductItem)
```

### ❌ DO NOT CREATE shared dto folder
```
feature/
└── {domain}/
    ├── dto/                           # ❌ WRONG - Don't create shared DTOs
    │   └── {Domain}Dto.java           # ❌ WRONG
```

### Naming Convention for Folders
- Use **snake_case** for feature folder names: `create_product`, `list_products`, `get_order`
- Use **PascalCase** for class names: `CreateProductController`, `ListProductsService`

---

## 📁 File Templates

### 1. Controller (extends BaseController)

```java
package com.vsa.ecommerce.feature.{domain}.{feature_name};

import com.vsa.ecommerce.common.abstraction.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/{domain}s")
@RequiredArgsConstructor
@Tag(name = "{Domain}")
public class {FeatureName}Controller extends BaseController {

    private final {FeatureName}Service service;

    @Operation(summary = "{Feature description}")
    @PostMapping // or @GetMapping, @PutMapping, @DeleteMapping
    @PreAuthorize("hasRole('USER')") // or 'ADMIN' or remove for public
    public ResponseEntity<{ResponseType}> handle(@Valid @RequestBody {FeatureName}Request request) {
        return ResponseEntity.ok(service.execute(request));
    }
}
```

### 2. Service (implements IService<Request, Response>)

```java
package com.vsa.ecommerce.feature.{domain}.{feature_name};

import com.vsa.ecommerce.common.abstraction.IService;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional // or @Transactional(readOnly = true) for queries
public class {FeatureName}Service implements IService<{FeatureName}Request, {ResponseType}> {

    private final {FeatureName}Repository repository;

    @Override
    public {ResponseType} execute({FeatureName}Request request) {
        // Business logic here
        // Use BusinessException for errors: throw new BusinessException(BusinessStatus.XXX);
    }
}
```

### 3. Repository (using EntityManager - PREFERRED)

**⚠️ IMPORTANT: Each feature should have its OWN repository using EntityManager directly.**

```java
package com.vsa.ecommerce.feature.{domain}.{feature_name};

import com.vsa.ecommerce.domain.entity.{Entity};
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository("Feature{FeatureName}Repository")  // Use unique name to avoid conflicts
public class {FeatureName}Repository {

    @PersistenceContext
    private EntityManager entityManager;

    // Find by ID
    public Optional<{Entity}> findById(Long id) {
        return Optional.ofNullable(entityManager.find({Entity}.class, id));
    }

    // Save (persist or merge)
    public {Entity} save({Entity} entity) {
        if (entity.getId() == null) {
            entityManager.persist(entity);
            return entity;
        } else {
            return entityManager.merge(entity);
        }
    }

    // Custom JPQL query
    public List<{Entity}> findByCondition(String param) {
        return entityManager.createQuery(
                "SELECT e FROM {Entity} e WHERE e.field = :param", {Entity}.class)
                .setParameter("param", param)
                .getResultList();
    }

    // Pagination
    public List<{Entity}> findAll(int page, int size) {
        return entityManager.createQuery("SELECT e FROM {Entity} e", {Entity}.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    // Count
    public long count() {
        return entityManager.createQuery("SELECT COUNT(e) FROM {Entity} e", Long.class)
                .getSingleResult();
    }
}
```

### 4. Repository (using BaseRepository - ONLY when necessary)

**Use BaseRepository ONLY when you need:**
- Complex Specification queries
- JpaRepository built-in methods extensively
- Batch operations from BaseRepository

```java
package com.vsa.ecommerce.feature.{domain}.{feature_name};

import com.vsa.ecommerce.common.repository.BaseRepository;
import com.vsa.ecommerce.domain.entity.{Entity};
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface {FeatureName}Repository extends BaseRepository<{Entity}, Long> {
    Optional<{Entity}> findByEmail(String email);
}
```

### 5. Request (implements Request)

```java
package com.vsa.ecommerce.feature.{domain}.{feature_name};

import com.vsa.ecommerce.common.abstraction.Request;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class {FeatureName}Request implements Request {

    @NotBlank(message = "Field is required")
    private String field;

    @NotNull
    @Positive
    private Long id;
}
```

### 6. Response (implements Response)

```java
package com.vsa.ecommerce.feature.{domain}.{feature_name};

import com.vsa.ecommerce.common.abstraction.Response;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class {FeatureName}Response implements Response {
    private Long id;
    private String name;
}
```

---

## 🔑 Repository Guidelines

### ✅ PREFERRED: EntityManager (Feature-specific Repository)

```java
@Repository("Feature{FeatureName}Repository")
public class {FeatureName}Repository {
    @PersistenceContext
    private EntityManager entityManager;
    // ...
}
```

**Benefits:**
- Complete isolation per feature
- No shared state between features
- Easier to maintain and test
- Clear dependency on what each feature needs

### ⚠️ FALLBACK: BaseRepository (Only when necessary)

```java
@Repository
public interface {FeatureName}Repository extends BaseRepository<{Entity}, Long> {
    // Spring Data JPA methods
}
```

**Use when:**
- Need complex Specification queries
- Need batch operations (batchSave, etc.)
- Heavy use of JpaRepository methods
- Multiple features genuinely share the same queries

---

## 🎯 Common Patterns

### Error Handling
```java
// Use BusinessException with BusinessStatus enum
throw new BusinessException(BusinessStatus.PRODUCT_NOT_FOUND);
throw new BusinessException(BusinessStatus.UNAUTHORIZED);
throw new BusinessException(BusinessStatus.INSUFFICIENT_STOCK);
```

### Get Current User
```java
Long userId = SecurityUtils.getCurrentUserId()
    .orElseThrow(() -> new BusinessException(BusinessStatus.UNAUTHORIZED));
```

### Empty Request/Response
```java
// For features with no request body
public class {FeatureName}Service implements IService<EmptyRequest, {ResponseType}> { }

// For features with no response body
public class {FeatureName}Service implements IService<{RequestType}, EmptyResponse> {
    return EmptyResponse.INSTANCE;
}
```

### Pagination Response
```java
@Data
@Builder
public class {FeatureName}Response implements Response {
    private List<{ItemDto}> items;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}
```

---

## 🛡️ Security Annotations

```java
@PreAuthorize("hasRole('ADMIN')")     // Admin only
@PreAuthorize("hasRole('USER')")      // Authenticated users
@PreAuthorize("permitAll()")          // Public endpoint
// No annotation = requires authentication by default (check security config)
```

---

## ⚡ Special Annotations

### Rate Limiting
```java
@RateLimit(maxRequests = 5)  // Max 5 requests per time window
```

### Idempotency (for POST/PUT operations)
```java
@Idempotent(keyPrefix = "order:create:")
```

### Caching (for read operations)
```java
@Cacheable(value = "products", key = "#id")
@CacheEvict(value = "products", key = "#id")
```

---

## 📝 Coding Standards

### Naming Conventions
- **Classes**: PascalCase (`CreateProductService`)
- **Methods/Variables**: camelCase (`findById`, `productName`)
- **Constants**: UPPER_SNAKE_CASE (`MAX_RETRY_COUNT`)
- **Packages**: lowercase (`com.vsa.ecommerce.feature.product`)
- **Feature Folders**: snake_case (`create_product`, `list_orders`)

### Lombok Usage
- Use `@RequiredArgsConstructor` for constructor injection
- Use `@Data` for DTOs and Request/Response classes
- Use `@Builder` for complex object creation
- Use `@Slf4j` for logging

### Transaction Management
- `@Transactional` on Service class for write operations
- `@Transactional(readOnly = true)` for read-only operations
- Never put `@Transactional` on Controller

### Validation
- Use Jakarta Validation annotations on Request fields
- Use `@Valid` on Controller method parameters

---

## 🚫 Anti-Patterns to Avoid

1. **❌ Don't create shared DTOs folder** - Each feature has its own Response class
2. **❌ Don't share repositories between features** (unless using BaseRepository as fallback)
3. **❌ Don't put business logic in Controller** - Controllers should only delegate to Service
4. **❌ Don't use field injection** - Use constructor injection via `@RequiredArgsConstructor`
5. **❌ Don't return Entity directly** - Always map to Response
6. **❌ Don't catch generic Exception** - Use specific exceptions or BusinessException
7. **❌ Don't create circular dependencies** between features

---

## ✅ Checklist for New Feature

- [ ] Create feature folder: `feature/{domain}/{feature_name}/`
- [ ] Create Controller extending `BaseController`
- [ ] Create Service implementing `IService<Request, Response>`
- [ ] Create Repository using `EntityManager` (preferred) or `BaseRepository` (fallback)
- [ ] Create Request implementing `Request`
- [ ] Create Response implementing `Response` (MUST be feature-specific, NO shared DTOs)
- [ ] Add `@Tag` for Swagger grouping
- [ ] Add `@PreAuthorize` for security
- [ ] Add `@Transactional` on Service
- [ ] Add validation annotations on Request
- [ ] Use `BusinessException` for error handling
