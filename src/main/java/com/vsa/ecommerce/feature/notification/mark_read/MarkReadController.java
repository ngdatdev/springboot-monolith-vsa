package com.vsa.ecommerce.feature.notification.mark_read;

import com.vsa.ecommerce.common.abstraction.BaseController;
import com.vsa.ecommerce.common.abstraction.EmptyResponse;
import com.vsa.ecommerce.common.security.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification")
public class MarkReadController extends BaseController {

    private final MarkReadService service;

    @PutMapping("/{id}/read")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<EmptyResponse> markAsRead(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new com.vsa.ecommerce.common.exception.BusinessException(
                        com.vsa.ecommerce.common.exception.BusinessStatus.UNAUTHORIZED));
        return ResponseEntity.ok(service.execute(MarkReadRequest.builder().notificationId(id).userId(userId).build()));
    }
}
