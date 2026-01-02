package com.vsa.ecommerce.feature.order.cancel_order;

import com.vsa.ecommerce.common.abstraction.Response;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CancelOrderResponse implements Response {
    private String orderId;
    private String status;
}
