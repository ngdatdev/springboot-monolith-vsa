package com.vsa.ecommerce.feature.payment.confirm_payment;

import com.vsa.ecommerce.common.abstraction.Request;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmPaymentRequest implements Request {
    private String transactionId;
    private boolean success;
}
