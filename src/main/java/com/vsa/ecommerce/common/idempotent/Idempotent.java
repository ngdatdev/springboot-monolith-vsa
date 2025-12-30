package com.vsa.ecommerce.common.idempotent;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Annotation to ensure a method is idempotent.
 * Usually applied to POST/PUT requests.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * Key prefix for Redis storage.
     */
    String keyPrefix() default "idempotent:";

    /**
     * Expiration time for the idempotency key.
     */
    long expireTime() default 24;

    /**
     * Time unit for expiration.
     */
    TimeUnit unit() default TimeUnit.HOURS;

    /**
     * Header name to extract the idempotency key from.
     */
    String headerName() default "X-Idempotency-Key";

    /**
     * Whether to return the cached response or throw an error on duplicate request.
     * Default is false (throw error).
     */
    boolean returnCachedResponse() default false;
}
