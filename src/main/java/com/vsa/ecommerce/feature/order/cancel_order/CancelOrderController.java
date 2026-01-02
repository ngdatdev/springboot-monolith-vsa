package com.vsa.ecommerce.feature.order.cancel_order;

import com.vsa.ecommerce.common.abstraction.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Order", description = "Order Management APIs")
public class CancelOrderController extends BaseController {

    private final CancelOrderService cancelOrderService;

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CancelOrderResponse> cancelOrder(@PathVariable Long id,
            @RequestBody CancelOrderRequest request) {
        request.setOrderId(id);
        CancelOrderResponse response = cancelOrderService.execute(request);
        return ResponseEntity.ok(response);
    }
}
