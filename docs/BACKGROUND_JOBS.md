# Background Job Framework

## 📦 Overview

Comprehensive background job framework using **Quartz Scheduler** for running scheduled and periodic tasks.

---

## 🎯 Features

- ✅ **Easy job creation** - Extend BaseQuartzJob
- ✅ **Automatic retry** - Configurable retry count and delay
- ✅ **Execution tracking** - Automatic logging and timing
- ✅ **Cron scheduling** - Flexible schedule with cron expressions
- ✅ **Job management** - Pause, resume, trigger, delete jobs
- ✅ **Concurrent execution control** - Prevent job overlap
- ✅ **Thread-safe** - Built on Quartz clustering support

---

## 💻 Creating a New Job

### **Step 1: Create Job Class**

```java
package com.vsa.ecommerce.job;

import com.vsa.ecommerce.common.job.BaseQuartzJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@DisallowConcurrentExecution  // Optional: prevent concurrent execution
public class MyCustomJob extends BaseQuartzJob {
    
    @Autowired
    private MyService myService;
    
    @Override
    public String getJobName() {
        return "MyCustomJob";
    }
    
    @Override
    public String getJobDescription() {
        return "Does something important every hour";
    }
    
    @Override
    protected void execute(JobExecutionContext context) throws Exception {
        log.info("Executing custom job logic");
        myService.doSomething();
    }
    
    @Override
    public int getMaxRetries() {
        return 5;  // Optional: custom retry count
    }
}
```

### **Step 2: Schedule the Job**

Add to `JobSchedulerConfig.java`:

```java
@PostConstruct
public void scheduleJobs() {
    // ... existing jobs
    
    // Schedule your custom job
    jobManagementService.scheduleJob(
        MyCustomJob.class,
        "0 0 * * * ?"  // Every hour
    );
}
```

**That's it!** Your job will run automatically. 🎉

---

## 📅 Cron Expression Examples

```
"0 0 * * * ?"         # Every hour at minute 0
"0 0/30 * * * ?"      # Every 30 minutes
"0 0 9 * * ?"         # Every day at 9:00 AM
"0 0 0 * * ?"         # Every day at midnight
"0 0 9 * * MON-FRI"   # Weekdays at 9:00 AM
"0 0 0 1 * ?"         # First day of every month at midnight
"0 0 2 * * SUN"       # Every Sunday at 2:00 AM
```

**Format:** `second minute hour day month weekday`

---

## 🛠️ Job Management API

### **Trigger Job Manually**

```java
@Autowired
private JobManagementService jobService;

// Trigger job immediately
jobService.triggerJob("MyCustomJob");
```

### **Pause/Resume Job**

```java
// Pause a job
jobService.pauseJob("MyCustomJob");

// Resume a paused job
jobService.resumeJob("MyCustomJob");
```

### **Delete Job**

```java
jobService.deleteJob("MyCustomJob");
```

### **Check if Job is Running**

```java
boolean isRunning = jobService.isJobRunning("MyCustomJob");
```

### **Schedule with Interval (Alternative to Cron)**

```java
// Run every 60 seconds
jobService.scheduleJobWithInterval(MyCustomJob.class, 60);
```

---

## 📊 Built-in Example Jobs

### **1. CacheCleanupJob**
- **Schedule:** Every hour (`0 0 * * * ?`)
- **Purpose:** Clean up expired cache entries
- **Status:** Ready to implement

### **2. DailySalesReportJob**
- **Schedule:** Every day at 9 AM (`0 0 9 * * ?`)
- **Purpose:** Send daily sales report email
- **Status:** Ready to implement

### **3. ExpiredOrderCleanupJob**
- **Schedule:** Every 30 minutes (`0 0/30 * * * ?`)
- **Purpose:** Cancel orders pending for > 24 hours
- **Status:** Ready to implement

---

## ⚙️ Configuration

### **application.yml**

```yaml
# Enable/Disable all background jobs
jobs:
  enabled: true  # Set to false to disable all jobs
```

### **Disable Jobs in Development**

```yaml
# application-dev.yml
jobs:
  enabled: false
```

---

## 🔧 Advanced Features

### **Prevent Concurrent Execution**

Add `@DisallowConcurrentExecution` to your job class:

```java
@DisallowConcurrentExecution
public class MyJob extends BaseQuartzJob {
    // This job will never run concurrently
}
```

### **Custom Retry Configuration**

```java
@Override
public int getMaxRetries() {
    return 5;  // Retry up to 5 times
}

@Override
public long getRetryDelayMs() {
    return 10000L;  // Wait 10 seconds between retries
}
```

### **Disable Job Tracking (for high-frequency jobs)**

```java
@Override
public boolean isTracked() {
    return false;  // Don't log execution to database
}
```

---

## 📈 Best Practices

### **1. Keep Jobs Idempotent**

Jobs should be safe to run multiple times:

```java
@Override
protected void execute(JobExecutionContext context) throws Exception {
    // ✅ Good: Check if work is already done
    if (!order.isProcessed()) {
        processOrder(order);
        order.setProcessed(true);
    }
    
    // ❌ Bad: Blindly process without checking
    // processOrder(order);  // Might process twice!
}
```

### **2. Use @DisallowConcurrentExecution for Data-Modifying Jobs**

```java
@DisallowConcurrentExecution  // Prevent race conditions
public class InventoryUpdateJob extends BaseQuartzJob {
    // Safe: Won't run concurrently
}
```

### **3. Log Important Information**

```java
@Override
protected void execute(JobExecutionContext context) throws Exception {
    log.info("Processing {} orders", orderCount);
    
    // Do work...
    
    log.info("Processed {} orders successfully", processedCount);
}
```

### **4. Handle Exceptions Gracefully**

```java
@Override
protected void execute(JobExecutionContext context) throws Exception {
    try {
        riskyOperation();
    } catch (SpecificException e) {
        log.error("Expected error occurred", e);
        // Handle gracefully, don't let it fail the job
    }
}
```

---

## 🏗️ Architecture

```
JobSchedulerConfig
    ↓ (schedules on startup)
JobManagementService
    ↓ (manages)
Quartz Scheduler
    ↓ (executes)
BaseQuartzJob (Abstract)
    ↓ (implements retry logic)
YourCustomJob (Concrete)
    ↓ (business logic)
Your Services
```

---

## 🐛 Troubleshooting

### **Job Not Running**

1. Check if jobs are enabled:
   ```yaml
   jobs:
     enabled: true
   ```

2. Check application logs for scheduling errors

3. Verify cron expression is valid:
   ```java
   // Test cron expression
   CronExpression.isValidExpression("0 0 * * * ?");
   ```

### **Job Running Multiple Times**

- Add `@DisallowConcurrentExecution` to job class
- Check if you have multiple instances without clustering

### **Job Failing Silently**

- Check logs for exceptions
- Verify all dependencies are injected correctly
- Test job manually: `jobService.triggerJob("MyJob")`

---

## 🔍 Monitoring Jobs

### **View Currently Executing Jobs**

```java
@Autowired
private Scheduler scheduler;

List<JobExecutionContext> runningJobs = scheduler.getCurrentlyExecutingJobs();
for (JobExecutionContext ctx : runningJobs) {
    System.out.println("Running: " + ctx.getJobDetail().getKey().getName());
}
```

### **Get Job Details**

```java
JobKey jobKey = new JobKey("MyJob", "default");
JobDetail jobDetail = scheduler.getJobDetail(jobKey);
System.out.println(jobDetail.getDescription());
```

---

## ✅ Quick Reference

| Task | Code |
|------|------|
| Create job | Extend `BaseQuartzJob` |
| Schedule job | Add to `JobSchedulerConfig` |
| Trigger manually | `jobService.triggerJob("name")` |
| Pause job | `jobService.pauseJob("name")` |
| Resume job | `jobService.resumeJob("name")` |
| Delete job | `jobService.deleteJob("name")` |
| Prevent concurrent | `@DisallowConcurrentExecution` |
| Custom retry | Override `getMaxRetries()` |

---

## 📚 Resources

- [Quartz Scheduler Documentation](http://www.quartz-scheduler.org/documentation/)
- [Cron Expression Generator](https://www.freeformatter.com/cron-expression-generator-quartz.html)
- [Quartz Cron Tutorial](http://www.quartz-scheduler.org/documentation/quartz-2.3.0/tutorials/crontrigger.html)

---

## 🚀 Next Steps

1. Implement business logic in example jobs
2. Add job-specific configuration properties
3. Create admin API to manage jobs dynamically
4. Add job execution history database table
5. Set up monitoring/alerting for job failures
