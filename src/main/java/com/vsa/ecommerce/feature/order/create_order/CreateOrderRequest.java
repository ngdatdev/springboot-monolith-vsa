package com.vsa.ecommerce.feature.order.create_order;

import com.vsa.ecommerce.common.abstraction.Request;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@Setter
public class CreateOrderRequest implements Request {
    private List<OrderItemDto> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDto {
        private Long productId;
        private int quantity;
    }
}
