package com.vsa.ecommerce.feature.notification.get_my_notifications;

import com.vsa.ecommerce.common.abstraction.Request;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetMyNotificationsRequest implements Request {
    private Long userId; // Extracted from token
    private int page;
    private int size;
}
