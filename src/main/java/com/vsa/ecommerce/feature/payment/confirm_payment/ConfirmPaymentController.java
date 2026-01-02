package com.vsa.ecommerce.feature.payment.confirm_payment;

import com.vsa.ecommerce.common.abstraction.BaseController;
import com.vsa.ecommerce.common.abstraction.EmptyResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment")
public class ConfirmPaymentController extends BaseController {

    private final ConfirmPaymentService service;

    @PostMapping("/confirm")
    public ResponseEntity<EmptyResponse> confirmPayment(@RequestBody ConfirmPaymentRequest request) {
        // Public endpoint for webhook / mock callback
        return ResponseEntity.ok(service.execute(request));
    }
}
