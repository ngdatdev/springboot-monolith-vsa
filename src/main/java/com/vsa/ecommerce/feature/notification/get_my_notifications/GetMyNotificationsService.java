package com.vsa.ecommerce.feature.notification.get_my_notifications;

import com.vsa.ecommerce.common.abstraction.IService;
import com.vsa.ecommerce.domain.entity.Notification;
import com.vsa.ecommerce.feature.notification.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetMyNotificationsService implements IService<GetMyNotificationsRequest, GetMyNotificationsResponse> {

    private final GetMyNotificationsRepository repository;

    @Override
    @Transactional(readOnly = true)
    public GetMyNotificationsResponse execute(GetMyNotificationsRequest request) {
        List<Notification> notifications = repository.findAllByUserId(request.getUserId(), request.getPage(),
                request.getSize());
        long total = repository.countByUserId(request.getUserId());

        List<NotificationDto> dtos = notifications.stream().map(this::mapToDto).collect(Collectors.toList());
        return GetMyNotificationsResponse.builder().notifications(dtos).totalElements(total).build();
    }

    private NotificationDto mapToDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .userId(notification.getRecipient().getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .status(notification.getStatus())
                .metadata(notification.getPayloadJson())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
