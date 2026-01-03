package com.vsa.ecommerce.feature.notification.mark_read;

import com.vsa.ecommerce.common.abstraction.EmptyResponse;
import com.vsa.ecommerce.common.abstraction.IService;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.domain.entity.Notification;
import com.vsa.ecommerce.domain.enums.NotificationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MarkReadService implements IService<MarkReadRequest, EmptyResponse> {

    private final MarkReadRepository repository;

    @Override
    @Transactional
    public EmptyResponse execute(MarkReadRequest request) {
        Notification notification = repository.findById(request.getNotificationId())
                .orElseThrow(() -> new BusinessException(BusinessStatus.NOT_FOUND));

        if (!notification.getRecipient().getId().equals(request.getUserId())) {
            throw new BusinessException(BusinessStatus.FORBIDDEN);
        }

        notification.setStatus(NotificationStatus.READ); // Assuming READ status exists
        // Wait, Need to check enum

        repository.save(notification);
        return new EmptyResponse();
    }
}
