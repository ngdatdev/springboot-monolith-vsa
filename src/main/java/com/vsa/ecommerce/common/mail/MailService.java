package com.vsa.ecommerce.common.mail;

import java.util.Map;

/**
 * Interface for Mail Service.
 */
public interface MailService {

    /**
     * Send plain text email.
     */
    void sendPlainTextEmail(String to, String subject, String content);

    /**
     * Send HTML email using Thymeleaf template.
     */
    void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> templateVariables);

    /**
     * Send welcome email to new user.
     */
    void sendWelcomeEmail(String to, String username);

    /**
     * Send password reset email.
     */
    void sendPasswordResetEmail(String to, String resetLink);

    /**
     * Send order confirmation email.
     */
    void sendOrderConfirmationEmail(String to, String orderNumber, String totalAmount);
}
