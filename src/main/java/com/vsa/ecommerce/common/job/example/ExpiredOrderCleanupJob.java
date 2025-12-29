package com.vsa.ecommerce.common.job.example;

import com.vsa.ecommerce.common.job.BaseQuartzJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

/**
 * Example job: Check and cancel expired pending orders.
 * 
 * Runs every 30 minutes.
 * Cron: "0 0/30 * * * ?"
 */
import com.vsa.ecommerce.common.job.QuartzJob;

@Slf4j
@Component
@DisallowConcurrentExecution
@QuartzJob(cron = "0 0/30 * * * ?", name = "ExpiredOrderCleanupJob", description = "Cancels orders pending for more than 24 hours every 30 minutes")
public class ExpiredOrderCleanupJob extends BaseQuartzJob {

    // @Autowired
    // private OrderRepository orderRepository;

    @Override
    public String getJobName() {
        return "ExpiredOrderCleanupJob";
    }

    @Override
    public String getJobDescription() {
        return "Cancels orders pending for more than 24 hours every 30 minutes";
    }

    @Override
    public void executeJob(JobExecutionContext context) throws Exception {
        log.info("Checking for expired orders...");

        // TODO: Implement expired order cleanup
        // Example:
        // LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        // List<Order> expiredOrders = orderRepository
        // .findByStatusAndCreatedAtBefore(OrderStatus.PENDING, cutoff);
        //
        // for (Order order : expiredOrders) {
        // order.setStatus(OrderStatus.CANCELLED);
        // orderRepository.save(order);
        //
        // // Send notification
        // mailService.sendOrderCancelledEmail(order.getUser().getEmail(),
        // order.getId());
        // }

        int cancelledCount = 0; // Placeholder

        log.info("Expired order cleanup completed. Cancelled {} orders", cancelledCount);
    }

    @Override
    public int getMaxRetries() {
        return 5; // Allow more retries for critical job
    }
}
