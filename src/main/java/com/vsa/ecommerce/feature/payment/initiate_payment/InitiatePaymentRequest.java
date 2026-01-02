package com.vsa.ecommerce.feature.payment.initiate_payment;

import com.vsa.ecommerce.common.abstraction.Request;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitiatePaymentRequest implements Request {
    private Long orderId;
    private String paymentMethod; // e.g., "STRIPE", "MOCK"
}
