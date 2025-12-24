package com.vsa.ecommerce.common.job;

import org.quartz.Job;

/**
 * Base interface for all background jobs.
 * 
 * All background jobs should implement this interface or extend BaseQuartzJob.
 * 
 * Features:
 * - Automatic job execution tracking
 * - Built-in retry mechanism
 * - Job history logging
 * - Distributed job execution (with Quartz clustering)
 * 
 * Usage:
 * 
 * <pre>
 * &#64;Component
 * public class MyJob extends BaseQuartzJob {
 * 
 *     @Override
 *     protected void executeInternal(JobExecutionContext context) throws Exception {
 *         // Job logic here
 *         log.info("Executing MyJob!");
 *     }
 * }
 * </pre>
 */
public interface BackgroundJob extends Job {

    /**
     * Get job name.
     * Used for logging and tracking.
     */
    String getJobName();

    /**
     * Get job description.
     */
    String getJobDescription();

    /**
     * Maximum retry attempts on failure.
     * Default: 3
     */
    default int getMaxRetries() {
        return 3;
    }

    /**
     * Retry delay in milliseconds.
     * Default: 5 seconds
     */
    default long getRetryDelayMs() {
        return 5000L;
    }

    /**
     * Whether this job should be tracked in job history.
     * Default: true
     */
    default boolean isTracked() {
        return true;
    }
}
