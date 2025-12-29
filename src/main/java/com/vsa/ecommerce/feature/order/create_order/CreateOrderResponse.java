package com.vsa.ecommerce.feature.order.create_order;

import com.vsa.ecommerce.common.abstraction.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateOrderResponse implements Response {
    private String orderId;
}
