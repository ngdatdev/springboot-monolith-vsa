package com.vsa.ecommerce.feature.payment.get_order_payments;

import com.vsa.ecommerce.common.abstraction.BaseController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment")
public class GetOrderPaymentsController extends BaseController {

    private final GetOrderPaymentsService service;

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<GetOrderPaymentsResponse> getOrderPayments(@PathVariable Long orderId) {
        return ResponseEntity.ok(service.execute(GetOrderPaymentsRequest.builder().orderId(orderId).build()));
    }
}
