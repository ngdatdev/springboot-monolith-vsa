package com.vsa.ecommerce.feature.notification.mark_read;

import com.vsa.ecommerce.common.abstraction.Request;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MarkReadRequest implements Request {
    private Long notificationId;
    private Long userId; // For ownership check
}
