package com.vsa.ecommerce.common.idempotent;

import com.vsa.ecommerce.common.redis.KeyConvention;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.common.redis.BaseRedisService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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

    private final BaseRedisService redisService;
    private final KeyConvention keyConvention;

    @Around("@annotation(idempotent)")
    public Object validateIdempotency(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        HttpServletRequest request = getRequest()
                .orElseThrow(
                        () -> new BusinessException(BusinessStatus.INTERNAL_SERVER_ERROR, "Request context not found"));

        String idempotencyKey = request.getHeader(idempotent.headerName());

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            log.trace("Missing idempotency key for method: {}, skipping check",
                    joinPoint.getSignature().toShortString());
            return joinPoint.proceed();
        }

        String redisKey = keyConvention.buildKey(KeyConvention.RESOURCE_IDEMPOTENCY,
                idempotent.keyPrefix() + idempotencyKey);

        // 1. Try to set the key in Redis (processing lock)
        boolean success = redisService.setIfAbsent(redisKey, "processing",
                Duration.of(idempotent.expireTime(), idempotent.unit().toChronoUnit()));

        if (!success) {
            log.warn("Duplicate request detected for key: {}", idempotencyKey);
            throw new BusinessException(BusinessStatus.CONFLICT,
                    "Request is already being processed or has been completed.");
        }

        try {
            // 2. Execute the actual method
            Object result = joinPoint.proceed();

            // 3. Optional: Map result here if you want to cache the response for replay
            // For now, we just keep the "processing" (or could change to "completed")
            return result;

        } catch (BusinessException e) {
            // Business errors usually mean we should keep the idempotency key
            // because the request was "completed" (even if with failure)
            throw e;
        } catch (Throwable e) {
            // Internal errors/System errors: Clear the key so the user can retry
            // immediately
            log.error("Idempotent request failed with system error, clearing key: {}", redisKey);
            redisService.delete(redisKey);
            throw e;
        }
    }

    private Optional<HttpServletRequest> getRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest);
    }
}
