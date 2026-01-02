package com.vsa.ecommerce.feature.notification.send_notification;

import com.vsa.ecommerce.common.abstraction.Request;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendNotificationRequest implements Request {
    private Long userId; // Recipient
    private String title;
    private String message;
    private String type;
    private String payloadJson;
}
