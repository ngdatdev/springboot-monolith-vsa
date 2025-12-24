package com.vsa.ecommerce.common.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

/**
 * Mail Service for sending emails with HTML templates.
 * 
 * Features:
 * - Send plain text emails
 * - Send HTML emails with Thymeleaf templates
 * - Async email sending (non-blocking)
 * - Attachment support
 * 
 * Usage:
 * 
 * <pre>
 * mailService.sendHtmlEmail(
 *         "user@example.com",
 *         "Welcome to VSA!",
 *         "welcome-email",
 *         Map.of("username", "John Doe"));
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final MailProperties mailProperties;

    /**
     * Send plain text email.
     * 
     * @param to      Recipient email
     * @param subject Email subject
     * @param content Email body (plain text)
     */
    @Async
    public void sendPlainTextEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(mailProperties.getFromAddress());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, false); // false = plain text

            mailSender.send(message);
            log.info("Plain text email sent to: {}", to);

        } catch (MessagingException e) {
            log.error("Failed to send plain text email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Send HTML email using Thymeleaf template.
     * 
     * @param to                Recipient email
     * @param subject           Email subject
     * @param templateName      Thymeleaf template name (without .html extension)
     * @param templateVariables Variables to pass to template
     */
    @Async
    public void sendHtmlEmail(String to, String subject, String templateName,
            Map<String, Object> templateVariables) {
        try {
            // Process Thymeleaf template
            Context context = new Context();
            context.setVariables(templateVariables);
            String htmlContent = templateEngine.process("email/" + templateName, context);

            // Create email message
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(mailProperties.getFromAddress());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML

            mailSender.send(message);
            log.info("HTML email sent to: {} using template: {}", to, templateName);

        } catch (MessagingException e) {
            log.error("Failed to send HTML email to: {} using template: {}", to, templateName, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Send welcome email to new user.
     */
    public void sendWelcomeEmail(String to, String username) {
        Map<String, Object> variables = Map.of(
                "username", username,
                "appName", "VSA E-Commerce");

        sendHtmlEmail(to, "Welcome to VSA E-Commerce!", "welcome", variables);
    }

    /**
     * Send password reset email.
     */
    public void sendPasswordResetEmail(String to, String resetLink) {
        Map<String, Object> variables = Map.of(
                "resetLink", resetLink,
                "appName", "VSA E-Commerce");

        sendHtmlEmail(to, "Reset Your Password", "password-reset", variables);
    }

    /**
     * Send order confirmation email.
     */
    public void sendOrderConfirmationEmail(String to, String orderNumber, String totalAmount) {
        Map<String, Object> variables = Map.of(
                "orderNumber", orderNumber,
                "totalAmount", totalAmount,
                "appName", "VSA E-Commerce");

        sendHtmlEmail(to, "Order Confirmation #" + orderNumber, "order-confirmation", variables);
    }
}
