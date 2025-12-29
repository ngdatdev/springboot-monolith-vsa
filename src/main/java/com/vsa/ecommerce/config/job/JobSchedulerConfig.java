package com.vsa.ecommerce.config.job;

import com.vsa.ecommerce.common.job.example.CacheCleanupJob;
import com.vsa.ecommerce.common.job.example.DailySalesReportJob;
import com.vsa.ecommerce.common.job.example.ExpiredOrderCleanupJob;
import com.vsa.ecommerce.common.job.JobManagementService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Job scheduler configuration.
 * Initializes and schedules all background jobs on application startup.
 * 
 * Jobs can be enabled/disabled via application.yml:
 * 
 * <pre>
 * jobs:
 *   enabled: true  # Master switch
 *   cache-cleanup:
 *     enabled: true
 *     cron: "0 0 * * * ?"
 * </pre>
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "jobs.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class JobSchedulerConfig {

    private final JobManagementService jobManagementService;

    @PostConstruct
    public void scheduleJobs() {
        log.info("Initializing background jobs...");

        try {
            // Schedule cache cleanup job - runs every hour
            jobManagementService.scheduleJob(
                    CacheCleanupJob.class,
                    "0 0 * * * ?" // Every hour at minute 0
            );

            // Schedule daily sales report - runs at 9 AM daily
            jobManagementService.scheduleJob(
                    DailySalesReportJob.class,
                    "0 0 9 * * ?" // 9:00 AM daily
            );

            // Schedule expired order cleanup - runs every 30 minutes
            jobManagementService.scheduleJob(
                    ExpiredOrderCleanupJob.class,
                    "0 0/30 * * * ?" // Every 30 minutes
            );

            log.info("All background jobs scheduled successfully");

        } catch (SchedulerException e) {
            log.error("Failed to schedule jobs", e);
            throw new RuntimeException("Job scheduling failed", e);
        }
    }
}
