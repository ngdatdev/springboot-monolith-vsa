package com.vsa.ecommerce.common.ratelimit;

import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RateLimitingService rateLimitingService;

    @Before("@annotation(rateLimit)")
    public void checkRateLimit(JoinPoint joinPoint, RateLimit rateLimit) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();

        String identifier = rateLimit.key();
        if (identifier.isEmpty()) {
            identifier = joinPoint.getSignature().toShortString() + ":" + getClientIp(request);
        }

        Duration window = Duration.of(rateLimit.window(), rateLimit.unit().toChronoUnit());

        boolean allowed = rateLimitingService.allowRequest(identifier, rateLimit.maxRequests(), window);

        if (!allowed) {
            log.warn("Rate limit exceeded for identifier: {}", identifier);
            throw new BusinessException(BusinessStatus.TOO_MANY_REQUESTS);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String remoteAddr = request.getHeader("X-Forwarded-For");
        if (remoteAddr == null || remoteAddr.isEmpty()) {
            remoteAddr = request.getRemoteAddr();
        }
        return remoteAddr;
    }
}
