package com.vsa.ecommerce.feature.order.admin_update_status;

import com.vsa.ecommerce.common.abstraction.BaseController;
import com.vsa.ecommerce.common.abstraction.EmptyResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@Tag(name = "Order Admin")
public class UpdateOrderStatusController extends BaseController {

    private final UpdateOrderStatusService service;

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmptyResponse> updateStatus(@PathVariable Long id,
            @RequestBody UpdateOrderStatusRequest request) {
        request.setOrderId(id);
        return ResponseEntity.ok(service.execute(request));
    }
}
