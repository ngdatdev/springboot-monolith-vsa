package com.vsa.ecommerce.feature.order.create_order;

import com.vsa.ecommerce.common.abstraction.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateOrderResponse implements IResponse {
    private String orderId;
}
