package com.vsa.ecommerce.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;

/**
 * Aspect for automated logging of Controller and Service methods.
 * Provides entry, exit, and execution time tracing.
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * Pointcut for all methods in classes annotated with @RestController
     * or @Service.
     */
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) || @within(org.springframework.stereotype.Service)")
    public void springManagedComponents() {
    }

    /**
     * Pointcut for methods or classes annotated with @NoLog.
     */
    @Pointcut("@annotation(com.vsa.ecommerce.common.logging.NoLog) || @within(com.vsa.ecommerce.common.logging.NoLog)")
    public void excludedFromLogging() {
    }

    /**
     * Around advice to log method execution details.
     */
    @Around("springManagedComponents() && !excludedFromLogging()")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        String fullName = className + "." + methodName;

        // Log entry
        log.info("[START] Execution: {}", fullName);
        if (log.isDebugEnabled()) {
            log.debug("[ARGS] {}: {}", fullName, Arrays.toString(joinPoint.getArgs()));
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            Object result = joinPoint.proceed();

            stopWatch.stop();
            log.info("[END] Execution: {} | Success | Duration: {}ms", fullName, stopWatch.getTotalTimeMillis());

            return result;
        } catch (Throwable throwable) {
            stopWatch.stop();
            log.error("[ERROR] Execution: {} | Failed | Duration: {}ms | Message: {}",
                    fullName, stopWatch.getTotalTimeMillis(), throwable.getMessage());
            throw throwable;
        }
    }
}
