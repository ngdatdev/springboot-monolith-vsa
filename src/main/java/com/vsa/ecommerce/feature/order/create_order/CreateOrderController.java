package com.vsa.ecommerce.feature.order.create_order;

import com.vsa.ecommerce.common.abstraction.BaseController;
import com.vsa.ecommerce.common.abstraction.Result;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class CreateOrderController extends BaseController {

    private final CreateOrderService createOrderService;

    @PostMapping
    @PermitAll
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        CreateOrderResponse response = createOrderService.execute(request);
        return ResponseEntity.ok(response);
    }
}
