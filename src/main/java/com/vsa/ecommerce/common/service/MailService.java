package com.vsa.monolith.common.service;

import java.util.Map;

public interface MailService {
    void sendTextEmail(String to, String subject, String body);
    void sendHtmlEmail(String to, String subject, String htmlBody);
    // Future: void sendTemplatedEmail(String to, String templateName, Map<String, Object> variables);
}
