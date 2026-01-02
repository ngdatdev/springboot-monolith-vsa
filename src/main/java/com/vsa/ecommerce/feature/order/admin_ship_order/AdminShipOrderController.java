package com.vsa.ecommerce.feature.order.admin_ship_order;

import com.vsa.ecommerce.common.abstraction.BaseController;
import com.vsa.ecommerce.common.abstraction.EmptyResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@Tag(name = "Order Admin")
public class AdminShipOrderController extends BaseController {

    private final AdminShipOrderService service;

    @PostMapping("/{id}/ship")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmptyResponse> shipOrder(@PathVariable Long id, @RequestBody AdminShipOrderRequest request) {
        request.setOrderId(id);
        return ResponseEntity.ok(service.execute(request));
    }
}
