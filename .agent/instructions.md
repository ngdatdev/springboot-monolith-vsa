# Project-Specific Instructions for Antigravity

Whenever you are asked to generate or modify source code in this project, you MUST first read and adhere to these rules.

## 1. Package Structure & Naming
- **Feature-Based Architecture**: Use `com.vsa.ecommerce.feature.[domain].[action]` for new features.
- **Unified Naming**:
    - `[Action]Controller`
    - `[Action]Service`
    - `[Action]Request` (implements `Request`)
    - `[Action]Response` (implements `Response`)
- **Domain Logic**: Use `com.vsa.ecommerce.domain.[entity]` for domain-specific models and logic that are reused across features.

## 2. Service Layer Pattern
- All service implementations MUST implement the `Service<TRequest, TResponse>` interface.
- Business logic MUST be placed in the `execute(TRequest request)` method.
- Follow the **Request-Response pattern** for all service methods.

## 3. Entity Standards
- All entities MUST extend `BaseEntity`.
- Use JPA auditing annotations (`@CreatedDate`, `@LastModifiedDate`, etc.) provided by the base class.
- Use Lombok `@Getter`, `@Setter`, and `@RequiredArgsConstructor`.

## 4. API & Controller Standards
- Controllers MUST extend `BaseController`.
- All successful responses will be automatically wrapped in `Result<T>` by `ResponseAdvice`.
- USE `ResponseEntity<TResponse>` as the return type in controllers.
- Use `@RestController`, `@RequestMapping`, and `@RequiredArgsConstructor`.

## 5. Error Handling
- DO NOT use generic `Exception`.
- USE `BusinessException` for business logic errors.
- Always provide a `BusinessStatus` enum value when throwing `BusinessException`.
- Example: `throw new BusinessException(BusinessStatus.NOT_FOUND);`

## 6. Utilities & Commons
When implementing new features, ALWAYS check and reuse these common components first:

### Security & Auth (`com.vsa.ecommerce.common.security`)
- `SecurityUtils`: Access current user ID, email, roles, permissions.
- `JwtProvider`: validtoken, getAuthentication...
- `UserPrincipal`: Custom UserDetails implementation.

### Repository Pattern (Strict VSA)
1. **Single Repository Per Feature**:
   - Each feature module MUST have exactly **one** repository class named `[FeatureName]Repository` (e.g., `CreateOrderRepository`).
   - Do **NOT** use generic domain repositories (e.g., `UserRepository`, `ProductRepository`) inside features.
   - Do **NOT** use Spring Data JPA interfaces (e.g., `extends JpaRepository` or `BaseRepository`).
   - **Use EntityManager Directly**: Inject `EntityManager` using `@PersistenceContext` and implement all data access logic (find, save, query) within this single class.
   - This ensures 100% decoupling and prevents leaking implementation details.

### Controller Documentation
1. **Swagger Tags**:
   - Every Controller MUST be annotated with `@Tag(name = "[Feature Name]", description = "[Description]")`.

### Data Access (`com.vsa.ecommerce.common.repository`)
- `BaseRepository<T, ID>`: Standard JPA repository with specific enhancements.
- **Naming Convention**: `[FeatureName]Repository` (e.g., `CreateOrderRepository`, `LoginRepository`).
- **Feature-Specific Repositories**: Create repositories inside the feature package (e.g., `feature/order/create_order/CreateOrderRepository.java`). Do NOT use generic naming like `OrderRepository` inside features.

### Caching (`com.vsa.ecommerce.common.cache`)
- `HybridCacheService`: Use for distributed (Redis) + local (Caffeine) caching.
- `LocalCacheService`: In-memory only.
- `RedisService`: Redis only operations.

### Abstraction (`com.vsa.ecommerce.common.abstraction`)
- `Service<R, T>`: Interface for all business logic services.
- `BaseController`: Helper methods for controllers.
- `Result<T>`: Standard API response wrapper.

### Exceptions (`com.vsa.ecommerce.common.exception`)
- `BusinessException`: Throw this for domain logical errors.
- `BusinessStatus`: Enum for error codes.

### Utilities (`com.vsa.ecommerce.common.util`)
- `JsonUtil`: JSON serialization/deserialization.
- `DateUtil`: Date/Time formatting.
- `StringUiil`: String manipulation.

## 7. Code Style
- **Explicit Imports**: ALWAYS import classes explicitly. Do NOT use fully qualified names (e.g., `com.vsa.ecommerce.domain.entity.Order order`) inside methods or signatures. Use `import com.vsa.ecommerce.domain.entity.Order;` and then `Order order`.
- Use **constructor injection** with `@RequiredArgsConstructor` (Final fields).
- Adhere to CamelCase for classes and variables.
- Use Snake Case for database column naming within `@Column`.
