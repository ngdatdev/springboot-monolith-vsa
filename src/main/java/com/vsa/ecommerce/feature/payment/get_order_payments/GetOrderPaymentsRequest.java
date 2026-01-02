package com.vsa.ecommerce.feature.payment.get_order_payments;

import com.vsa.ecommerce.common.abstraction.Request;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetOrderPaymentsRequest implements Request {
    private Long orderId;
}
