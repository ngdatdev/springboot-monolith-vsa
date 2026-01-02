package com.vsa.ecommerce.feature.notification.dto;

import com.vsa.ecommerce.common.abstraction.Response;
import com.vsa.ecommerce.domain.enums.NotificationStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class NotificationDto implements Response {
    private Long id;
    private Long userId;
    private String title;
    private String message;
    private String type; // e.g. ORDER_SHIPPED
    private NotificationStatus status;
    private String metadata; // JSON String
    private LocalDateTime createdAt;
}
