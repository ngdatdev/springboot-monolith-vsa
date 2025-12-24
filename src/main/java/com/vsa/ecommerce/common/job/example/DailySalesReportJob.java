package com.vsa.ecommerce.common.job.example;

import com.vsa.ecommerce.common.job.BaseQuartzJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

/**
 * Example job: Send daily sales report email.
 * 
 * Runs every day at 9:00 AM.
 * Cron: "0 0 9 * * ?"
 */
@Slf4j
@Component
@DisallowConcurrentExecution
public class DailySalesReportJob extends BaseQuartzJob {

    // @Autowired
    // private MailService mailService;

    // @Autowired
    // private OrderRepository orderRepository;

    @Override
    public String getJobName() {
        return "DailySalesReportJob";
    }

    @Override
    public String getJobDescription() {
        return "Sends daily sales report email at 9 AM";
    }

    @Override
    public void executeJob(JobExecutionContext context) throws Exception {
        log.info("Generating daily sales report...");

        // TODO: Implement sales report logic
        // Example:
        // LocalDate yesterday = LocalDate.now().minusDays(1);
        // List<Order> orders = orderRepository.findByDateCreatedBetween(yesterday,
        // LocalDate.now());
        // BigDecimal totalSales = orders.stream()
        // .map(Order::getTotalAmount)
        // .reduce(BigDecimal.ZERO, BigDecimal::add);
        //
        // mailService.sendHtmlEmail(
        // "admin@company.com",
        // "Daily Sales Report",
        // "sales-report",
        // Map.of("date", yesterday, "totalSales", totalSales, "orderCount",
        // orders.size())
        // );

        log.info("Daily sales report sent successfully");
    }
}
