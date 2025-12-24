package com.vsa.ecommerce.common.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Abstract base class for Quartz jobs.
 * 
 * Provides:
 * - Automatic exception handling
 * - Retry logic
 * - Execution time tracking
 * - Logging
 * 
 * Extend this class to create new background jobs:
 * 
 * <pre>
 * &#64;Component
 * &#64;DisallowConcurrentExecution // Optional: prevent concurrent execution
 * public class MyJob extends BaseQuartzJob {
 * 
 *     &#64;Autowired
 *     private MyService myService;
 * 
 *     &#64;Override
 *     public String getJobName() {
 *         return "MyJob";
 *     }
 * 
 *     &#64;Override
 *     public String getJobDescription() {
 *         return "Does something important";
 *     }
 * 
 *     @Override
 *     protected void executeInternal(JobExecutionContext context) throws Exception {
 *         log.info("Executing MyJob");
 *         myService.doSomething();
 *     }
 * }
 * </pre>
 */
@Slf4j
public abstract class BaseQuartzJob extends QuartzJobBean implements BackgroundJob {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        long startTime = System.currentTimeMillis();
        String jobName = getJobName();

        log.info("[{}] Starting job execution", jobName);

        try {
            // Execute the job
            executeJob(context);

            long duration = System.currentTimeMillis() - startTime;
            log.info("[{}] Job completed successfully in {}ms", jobName, duration);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("[{}] Job failed after {}ms: {}", jobName, duration, e.getMessage(), e);

            // Handle retry logic
            handleRetry(context, e);

            // Re-throw to let Quartz handle it
            JobExecutionException jee = new JobExecutionException(e);
            jee.setRefireImmediately(false); // Don't refire immediately
            throw jee;
        }
    }

    /**
     * Implement this method with your job logic.
     */
    public abstract void executeJob(JobExecutionContext context) throws Exception;

    /**
     * Handle retry logic.
     */
    private void handleRetry(JobExecutionContext context, Exception e) {
        int retryCount = getRetryCount(context);
        int maxRetries = getMaxRetries();

        if (retryCount < maxRetries) {
            int nextRetry = retryCount + 1;
            context.getJobDetail().getJobDataMap().put("retryCount", nextRetry);

            log.warn("[{}] Will retry ({}/{})", getJobName(), nextRetry, maxRetries);
        } else {
            log.error("[{}] Max retries ({}) exceeded, giving up", getJobName(), maxRetries);
        }
    }

    /**
     * Get current retry count from job context.
     */
    private int getRetryCount(JobExecutionContext context) {
        Integer count = (Integer) context.getJobDetail().getJobDataMap().get("retryCount");
        return count != null ? count : 0;
    }
}
