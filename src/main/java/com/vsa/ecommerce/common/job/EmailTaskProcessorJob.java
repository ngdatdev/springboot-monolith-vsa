package com.vsa.ecommerce.common.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsa.ecommerce.common.mail.MailServiceImpl;
import com.vsa.ecommerce.domain.entity.PersistentTask;
import com.vsa.ecommerce.feature.email.EmailTaskPayload;
import com.vsa.ecommerce.feature.system.PersistentTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Quartz Job to process pending email tasks from the PersistentTask table.
 * Runs every minute to check for new mail tasks.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@DisallowConcurrentExecution
@QuartzJob(cron = "0 * * * * ?", name = "EmailTaskProcessorJob", description = "Processes pending email tasks from the DB queue every minute")
public class EmailTaskProcessorJob extends BaseQuartzJob {

    private final PersistentTaskRepository taskRepository;
    private final MailServiceImpl mailService;
    private final ObjectMapper objectMapper;

    @Override
    public String getJobName() {
        return "EmailTaskProcessorJob";
    }

    @Override
    public String getJobDescription() {
        return "Processes pending email tasks from the database queue";
    }

    @Override
    public int getMaxRetries() {
        return 3;
    }

    @Override
    public void executeJob(JobExecutionContext context) throws Exception {
        List<PersistentTask> pendingTasks = taskRepository.findPendingTasks("EMAIL", LocalDateTime.now());

        if (pendingTasks.isEmpty()) {
            return;
        }

        log.info("Found {} pending email tasks to process", pendingTasks.size());

        for (PersistentTask task : pendingTasks) {
            processSingleTask(task);
        }
    }

    private void processSingleTask(PersistentTask task) {
        try {
            task.setStatus("PROCESSING");
            taskRepository.save(task);

            EmailTaskPayload payload = objectMapper.readValue(task.getPayload(), EmailTaskPayload.class);

            // Attempt to send
            mailService.performRealSend(payload);

            // Success
            task.setStatus("COMPLETED");
            task.setProcessedAt(LocalDateTime.now());
            taskRepository.save(task);
            log.info("Email task {} completed successfully", task.getId());

        } catch (Exception e) {
            log.error("Failed to process email task {}: {}", task.getId(), e.getMessage());
            handleTaskFailure(task, e);
        }
    }

    private void handleTaskFailure(PersistentTask task, Exception e) {
        int currentRetries = task.getRetryCount() != null ? task.getRetryCount() : 0;
        int nextRetries = currentRetries + 1;
        task.setRetryCount(nextRetries);

        String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
        String currentError = task.getLastError() != null ? task.getLastError() + " | " : "";
        task.setLastError(eShort(currentError + "Attempt " + nextRetries + ": " + errorMsg));

        if (nextRetries >= getMaxRetries()) {
            task.setStatus("FAILED");
            log.error("Email task {} reached max retries and FAILED", task.getId());

            // USER REQUEST: Send apology plaintext email on total failure
            sendApology(task);
        } else {
            task.setStatus("PENDING"); // Return to pending for next run
            log.warn("Email task {} will be retried (Attempt {})", task.getId(), nextRetries);
        }
        taskRepository.save(task);
    }

    private void sendApology(PersistentTask task) {
        try {
            EmailTaskPayload payload = objectMapper.readValue(task.getPayload(), EmailTaskPayload.class);
            mailService.sendApologyEmail(payload.getTo());
        } catch (Exception e) {
            log.error("Failed to send apology for task {}", task.getId(), e);
        }
    }

    private String eShort(String msg) {
        if (msg == null)
            return "Unknown error";
        return msg.length() > 2000 ? msg.substring(msg.length() - 1999) : msg;
    }
}
