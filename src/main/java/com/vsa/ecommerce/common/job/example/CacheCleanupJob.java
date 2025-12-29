package com.vsa.ecommerce.common.job.example;

import com.vsa.ecommerce.common.job.BaseQuartzJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

/**
 * Example job: Clean up expired cache entries.
 * 
 * Runs every hour at minute 0.
 * Cron: "0 0 * * * ?"
 * 
 * @DisallowConcurrentExecution prevents multiple instances running
 *                              simultaneously
 */
import com.vsa.ecommerce.common.job.QuartzJob;

@Slf4j
@Component
@DisallowConcurrentExecution
@QuartzJob(cron = "0 0 * * * ?", name = "CacheCleanupJob", description = "Cleans up expired cache entries hourly")
public class CacheCleanupJob extends BaseQuartzJob {

    @Override
    public String getJobName() {
        return "CacheCleanupJob";
    }

    @Override
    public String getJobDescription() {
        return "Cleans up expired cache entries hourly";
    }

    @Override
    public void executeJob(JobExecutionContext context) {
        log.info("Starting cache cleanup...");

        // TODO: Implement cache cleanup logic
        // Example:
        // cacheManager.evictExpired();
        // redisTemplate.delete(expiredKeys);

        int cleanedEntries = 0; // Placeholder

        log.info("Cache cleanup completed. Cleaned {} entries", cleanedEntries);
    }
}
