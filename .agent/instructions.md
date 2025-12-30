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
- **Pagination**: Use `PageResponse<T>` for paginated results.
- **Repository**: Use `BaseRepository` and `BaseRepositoryImpl` for custom JPA repository functionality if needed.
- **Cache**: Use `HybridCacheService` for distributed and local caching needs.
- **Validation**: Perform validation inside the Service's `execute` method using `BusinessException`.

## 7. Code Style
- Use **constructor injection** with `@RequiredArgsConstructor` (Final fields).
- Adhere to CamelCase for classes and variables.
- Use Snake Case for database column naming within `@Column`.
