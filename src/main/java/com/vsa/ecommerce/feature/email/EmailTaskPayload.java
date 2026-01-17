package com.vsa.ecommerce.feature.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Payload for email tasks stored in PersistentTask entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailTaskPayload {
    private String to;
    private String subject;
    private String content; // Plain text or processed HTML
    private String templateName; // Optional, if using template
    private Map<String, Object> templateVariables; // Optional
    private boolean isHtml;
}
