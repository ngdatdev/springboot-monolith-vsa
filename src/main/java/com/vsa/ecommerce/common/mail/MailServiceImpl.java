package com.vsa.ecommerce.common.mail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsa.ecommerce.config.mail.MailProperties;
import com.vsa.ecommerce.domain.entity.PersistentTask;
import com.vsa.ecommerce.feature.email.EmailTaskPayload;
import com.vsa.ecommerce.feature.system.PersistentTaskRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

/**
 * Mail Service for sending emails with HTML templates via Database Queue.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final MailProperties mailProperties;
    private final PersistentTaskRepository taskRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void sendPlainTextEmail(String to, String subject, String content) {
        EmailTaskPayload payload = EmailTaskPayload.builder()
                .to(to)
                .subject(subject)
                .content(content)
                .isHtml(false)
                .build();

        queueEmailTask(payload, "PLAIN_TEXT");
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String templateName,
            Map<String, Object> templateVariables) {

        EmailTaskPayload payload = EmailTaskPayload.builder()
                .to(to)
                .subject(subject)
                .templateName(templateName)
                .templateVariables(templateVariables)
                .isHtml(true)
                .build();

        queueEmailTask(payload, "HTML_TEMPLATE");
    }

    private void queueEmailTask(EmailTaskPayload payload, String taskType) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);

            PersistentTask task = PersistentTask.builder()
                    .category("EMAIL")
                    .taskType(taskType)
                    .status("PENDING")
                    .payload(jsonPayload)
                    .priority(0)
                    .retryCount(0)
                    .build();

            taskRepository.save(task);
            log.info("Email task queued for: {} (Type: {})", payload.getTo(), taskType);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize email payload for: {}", payload.getTo(), e);
            throw new RuntimeException("Failed to queue email", e);
        }
    }

    public void performRealSend(EmailTaskPayload payload) throws MessagingException {
        String content = payload.getContent();

        if (payload.isHtml() && payload.getTemplateName() != null && content == null) {
            Context context = new Context();
            if (payload.getTemplateVariables() != null) {
                context.setVariables(payload.getTemplateVariables());
            }
            content = templateEngine.process("email/" + payload.getTemplateName(), context);
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(mailProperties.getFromAddress());
        helper.setTo(payload.getTo());
        helper.setSubject(payload.getSubject());
        helper.setText(content, payload.isHtml());

        mailSender.send(message);
    }

    public void sendApologyEmail(String to) {
        try {
            log.warn("Sending service apology email to: {}", to);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(mailProperties.getFromAddress());
            helper.setTo(to);
            helper.setSubject("Service Notification: Email Delivery Issue");
            helper.setText("Chào bạn,\n\nChúng tôi rất tiếc vì sự cố kỹ thuật khiến việc xử lý email dịch vụ bị chậm " +
                    "hoặc thất bại. Đội ngũ kỹ thuật đang xử lý vấn đề này.\n\nTrân trọng,\nVSA Team.", false);

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Even apology email failed for: {}", to, e);
        }
    }

    public void sendWelcomeEmail(String to, String username) {
        sendHtmlEmail(to, "Welcome to VSA E-Commerce!", "welcome",
                Map.of("username", username, "appName", "VSA E-Commerce"));
    }

    public void sendPasswordResetEmail(String to, String resetLink) {
        sendHtmlEmail(to, "Reset Your Password", "password-reset",
                Map.of("resetLink", resetLink, "appName", "VSA E-Commerce"));
    }

    public void sendOrderConfirmationEmail(String to, String orderNumber, String totalAmount) {
        sendHtmlEmail(to, "Order Confirmation #" + orderNumber, "order-confirmation",
                Map.of("orderNumber", orderNumber, "totalAmount", totalAmount, "appName", "VSA E-Commerce"));
    }

    public void sendEmailVerificationOtp(String to, String otp) {
        sendHtmlEmail(to, "Verify Your Email", "verify-email", Map.of("otp", otp, "appName", "VSA E-Commerce"));
    }
}
