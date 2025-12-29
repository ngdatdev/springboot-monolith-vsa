package com.vsa.ecommerce.common.job;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a class as a scheduled Quartz job.
 * 
 * Jobs annotated with this will be automatically discovered and scheduled by
 * JobSchedulerConfig.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface QuartzJob {

    /**
     * Cron expression for scheduling the job.
     */
    String cron();

    /**
     * Optional name for the job. If empty, the class name will be used.
     */
    String name() default "";

    /**
     * Optional description for the job.
     */
    String description() default "";
}
