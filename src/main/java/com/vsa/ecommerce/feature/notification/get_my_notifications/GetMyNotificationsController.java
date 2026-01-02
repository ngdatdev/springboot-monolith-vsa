package com.vsa.ecommerce.feature.notification.get_my_notifications;

import com.vsa.ecommerce.common.abstraction.BaseController;
import com.vsa.ecommerce.common.security.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification")
public class GetMyNotificationsController extends BaseController {

    private final GetMyNotificationsService service;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<GetMyNotificationsResponse> getMyNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new com.vsa.ecommerce.common.exception.BusinessException(
                        com.vsa.ecommerce.common.exception.BusinessStatus.UNAUTHORIZED));
        return ResponseEntity.ok(service.execute(GetMyNotificationsRequest.builder()
                .userId(userId)
                .page(page)
                .size(size)
                .build()));
    }
}
