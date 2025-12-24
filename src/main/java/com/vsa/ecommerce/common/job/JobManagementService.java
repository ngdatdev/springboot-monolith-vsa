package com.vsa.ecommerce.common.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * Service for managing background jobs.
 * 
 * Provides methods to:
 * - Schedule new jobs
 * - Trigger jobs manually
 * - Pause/Resume jobs
 * - Delete jobs
 * 
 * Usage:
 * 
 * <pre>
 * @Autowired
 * private JobManagementService jobService;
 * 
 * // Schedule a job to run every 5 minutes
 * jobService.scheduleJob(MyJob.class, "0 0/5 * * * ?");
 * 
 * // Trigger a job immediately
 * jobService.triggerJob("MyJob");
 * </pre>
 */
@Slf4j
@Service
public class JobManagementService {

    private final Scheduler scheduler;
    private final ApplicationContext applicationContext;

    @Autowired
    public JobManagementService(Scheduler scheduler, ApplicationContext applicationContext) {
        this.scheduler = scheduler;
        this.applicationContext = applicationContext;
    }

    /**
     * Schedule a job with cron expression.
     * 
     * @param jobClass       Job class (must extend BaseQuartzJob)
     * @param cronExpression Cron expression (e.g., "0 0 * * * ?" for every hour)
     */
    public void scheduleJob(Class<? extends BackgroundJob> jobClass, String cronExpression)
            throws SchedulerException {

        BackgroundJob jobInstance = applicationContext.getBean(jobClass);
        String jobName = jobInstance.getJobName();
        String jobGroup = "default";

        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, jobGroup)
                .withDescription(jobInstance.getJobDescription())
                .storeDurably()
                .build();

        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobName + "-trigger", jobGroup)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
        log.info("Scheduled job: {} with cron: {}", jobName, cronExpression);
    }

    /**
     * Schedule a job with simple interval.
     * 
     * @param jobClass          Job class
     * @param intervalInSeconds Interval in seconds
     */
    public void scheduleJobWithInterval(Class<? extends BackgroundJob> jobClass, int intervalInSeconds)
            throws SchedulerException {

        BackgroundJob jobInstance = applicationContext.getBean(jobClass);
        String jobName = jobInstance.getJobName();
        String jobGroup = "default";

        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, jobGroup)
                .withDescription(jobInstance.getJobDescription())
                .storeDurably()
                .build();

        SimpleTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobName + "-trigger", jobGroup)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(intervalInSeconds)
                        .repeatForever())
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
        log.info("Scheduled job: {} with interval: {}s", jobName, intervalInSeconds);
    }

    /**
     * Trigger a job immediately.
     */
    public void triggerJob(String jobName) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, "default");
        scheduler.triggerJob(jobKey);
        log.info("Triggered job: {}", jobName);
    }

    /**
     * Pause a job.
     */
    public void pauseJob(String jobName) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, "default");
        scheduler.pauseJob(jobKey);
        log.info("Paused job: {}", jobName);
    }

    /**
     * Resume a paused job.
     */
    public void resumeJob(String jobName) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, "default");
        scheduler.resumeJob(jobKey);
        log.info("Resumed job: {}", jobName);
    }

    /**
     * Delete a job.
     */
    public void deleteJob(String jobName) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, "default");
        boolean deleted = scheduler.deleteJob(jobKey);
        if (deleted) {
            log.info("Deleted job: {}", jobName);
        } else {
            log.warn("Job not found: {}", jobName);
        }
    }

    /**
     * Check if a job is currently running.
     */
    public boolean isJobRunning(String jobName) throws SchedulerException {
        for (JobExecutionContext context : scheduler.getCurrentlyExecutingJobs()) {
            if (context.getJobDetail().getKey().getName().equals(jobName)) {
                return true;
            }
        }
        return false;
    }
}
