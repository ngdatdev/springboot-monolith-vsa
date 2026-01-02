package com.vsa.ecommerce.feature.order.cancel_order;

import com.vsa.ecommerce.common.abstraction.Request;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CancelOrderRequest implements Request {
    private Long orderId;
    private String reason;
}
