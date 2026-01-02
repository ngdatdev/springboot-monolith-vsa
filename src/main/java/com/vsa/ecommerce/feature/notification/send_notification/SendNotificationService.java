package com.vsa.ecommerce.feature.notification.send_notification;

import com.vsa.ecommerce.common.abstraction.EmptyResponse;
import com.vsa.ecommerce.common.abstraction.Service;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.domain.entity.Notification;
import com.vsa.ecommerce.domain.entity.User;
import com.vsa.ecommerce.domain.enums.NotificationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SendNotificationService implements Service<SendNotificationRequest, EmptyResponse> {

    private final SendNotificationRepository repository;

    @Override
    @Transactional
    public EmptyResponse execute(SendNotificationRequest request) {
        User user = repository.findUserById(request.getUserId())
                .orElseThrow(() -> new BusinessException(BusinessStatus.USER_NOT_FOUND));

        Notification notification = new Notification();
        notification.setRecipient(user);
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setType(request.getType());
        notification.setPayloadJson(request.getPayloadJson() != null ? request.getPayloadJson() : "{}");
        notification.setStatus(NotificationStatus.SENT); // Immediate logic
        notification.setSentAt(LocalDateTime.now());

        repository.save(notification);

        return new EmptyResponse();
    }
}
