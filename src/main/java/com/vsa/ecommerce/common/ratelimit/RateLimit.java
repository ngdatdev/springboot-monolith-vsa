package com.vsa.ecommerce.common.ratelimit;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Annotation to mark methods for rate limiting.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * Unique key for rate limiting. If empty, uses method name + remote IP.
     */
    String key() default "";

    /**
     * Maximum number of requests allowed within the window.
     */
    int maxRequests() default 10;

    /**
     * Time window duration.
     */
    long window() default 1;

    /**
     * Time unit for the window.
     */
    TimeUnit unit() default TimeUnit.MINUTES;
}
