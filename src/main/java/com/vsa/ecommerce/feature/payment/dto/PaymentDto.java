package com.vsa.ecommerce.feature.payment.dto;

import com.vsa.ecommerce.common.abstraction.Response;
import com.vsa.ecommerce.domain.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentDto implements Response {
    private Long id;
    private Long orderId; // Only ID to avoid nesting loop
    private String transactionId;
    private PaymentStatus status;
    private BigDecimal amount;
    private String failureReason;
    private LocalDateTime createdAt;
}
