package com.vsa.ecommerce.feature.order.create_order;

import com.vsa.ecommerce.common.abstraction.Request;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest implements Request {
    private String productId;
    private int quantity;
}
