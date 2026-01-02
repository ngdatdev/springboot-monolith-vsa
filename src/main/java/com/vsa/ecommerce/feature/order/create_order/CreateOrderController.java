package com.vsa.ecommerce.feature.order.create_order;

import com.vsa.ecommerce.common.abstraction.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import com.vsa.ecommerce.common.idempotent.Idempotent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Order", description = "Order Management APIs")
public class CreateOrderController extends BaseController {

    private final CreateOrderService createOrderService;

    @Idempotent(keyPrefix = "order:create:")
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        CreateOrderResponse response = createOrderService.execute(request);
        return ResponseEntity.ok(response);
    }
}
