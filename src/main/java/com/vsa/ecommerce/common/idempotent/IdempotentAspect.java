package com.vsa.ecommerce.common.idempotent;

import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.Optional;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class IdempotentAspect {

    private final RedisTemplate<String, Object> redisTemplate;

    @Before("@annotation(idempotent)")
    public void validateIdempotency(JoinPoint joinPoint, Idempotent idempotent) {
        HttpServletRequest request = getRequest()
                .orElseThrow(
                        () -> new BusinessException(BusinessStatus.INTERNAL_SERVER_ERROR, "Request context not found"));

        String idempotencyKey = request.getHeader(idempotent.headerName());

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            // If the key is missing, we might want to throw an error or skip.
            // In a strict API, it should be required for idempotent endpoints.
            log.warn("Missing idempotency key for method: {}", joinPoint.getSignature().toShortString());
            // throw new BusinessException(BusinessStatus.PARAM_ERROR, "Idempotency key is
            // required");
            return;
        }

        String redisKey = idempotent.keyPrefix() + idempotencyKey;

        // Try to set the key in Redis (pseudo-lock)
        // If it returns false, the key already exists (request is being processed or
        // was processed)
        Boolean success = redisTemplate.opsForValue().setIfAbsent(redisKey, "processing",
                Duration.of(idempotent.expireTime(), idempotent.unit().toChronoUnit()));

        if (Boolean.FALSE.equals(success)) {
            log.warn("Duplicate request detected for key: {}", idempotencyKey);
            throw new BusinessException(BusinessStatus.CONFLICT,
                    "Request is already being processed or has been completed.");
        }

        // Note: For full implementation with returnCachedResponse=true,
        // we would need an @Around advice to capture the result and store it in Redis.
        // For now, we provide the blocking mechanism.
    }

    private Optional<HttpServletRequest> getRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest);
    }
}
