package com.vsa.ecommerce.feature.payment.initiate_payment;

import com.vsa.ecommerce.common.abstraction.BaseController;
import com.vsa.ecommerce.feature.payment.dto.PaymentDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment")
public class InitiatePaymentController extends BaseController {

    private final InitiatePaymentService service;

    @PostMapping("/initiate")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaymentDto> initiatePayment(@RequestBody InitiatePaymentRequest request) {
        return ResponseEntity.ok(service.execute(request));
    }
}
