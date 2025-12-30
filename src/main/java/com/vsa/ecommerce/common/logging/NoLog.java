package com.vsa.ecommerce.common.logging;

import java.lang.annotation.*;

/**
 * Annotation to exclude specific methods or classes from automatic AOP logging.
 * Can be applied to a class to exclude all its methods, or to a specific
 * method.
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoLog {
}
