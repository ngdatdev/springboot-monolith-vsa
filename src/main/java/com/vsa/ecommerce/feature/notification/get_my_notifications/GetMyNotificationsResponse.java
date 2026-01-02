package com.vsa.ecommerce.feature.notification.get_my_notifications;

import com.vsa.ecommerce.common.abstraction.Response;
import com.vsa.ecommerce.feature.notification.dto.NotificationDto;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class GetMyNotificationsResponse implements Response {
    private List<NotificationDto> notifications;
    private long totalElements;
}
