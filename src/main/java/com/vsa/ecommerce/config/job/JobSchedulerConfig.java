package com.vsa.ecommerce.config.job;

import com.vsa.ecommerce.common.job.BackgroundJob;
import com.vsa.ecommerce.common.job.JobManagementService;
import com.vsa.ecommerce.common.job.QuartzJob;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Job scheduler configuration.
 * Initializes and schedules all background jobs on application startup.
 * 
 * Auto-discovers jobs annotated with @QuartzJob.
 * 
 * Jobs can be enabled/disabled via application.yml:
 * 
 * <pre>
 * jobs:
 *   enabled: true  # Master switch
 * </pre>
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "jobs.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class JobSchedulerConfig {

    private final JobManagementService jobManagementService;
    private final ApplicationContext applicationContext;

    @PostConstruct
    public void scheduleJobs() {
        log.info("Initializing background jobs...");

        Map<String, Object> quartzJobs = applicationContext.getBeansWithAnnotation(QuartzJob.class);

        if (quartzJobs.isEmpty()) {
            log.info("No jobs found with @QuartzJob annotation.");
            return;
        }

        quartzJobs.values().forEach(bean -> {
            if (bean instanceof BackgroundJob) {
                scheduleSingleJob((BackgroundJob) bean);
            } else {
                log.warn("Bean {} is annotated with @QuartzJob but does not extend BackgroundJob",
                        bean.getClass().getName());
            }
        });

        log.info("All background jobs scheduled successfully");
    }

    private void scheduleSingleJob(BackgroundJob job) {
        Class<?> jobClass = job.getClass();
        QuartzJob annotation = jobClass.getAnnotation(QuartzJob.class);
        String cronExpression = annotation.cron();
        String jobName = annotation.name().isEmpty() ? job.getJobName() : annotation.name();

        try {
            jobManagementService.scheduleJob(
                    (Class<? extends BackgroundJob>) jobClass,
                    cronExpression);
            log.info("Scheduled job: {} with cron: {}", jobName, cronExpression);
        } catch (SchedulerException e) {
            log.error("Failed to schedule job: {}", jobName, e);
            throw new RuntimeException("Job scheduling failed for " + jobName, e);
        }
    }
}
