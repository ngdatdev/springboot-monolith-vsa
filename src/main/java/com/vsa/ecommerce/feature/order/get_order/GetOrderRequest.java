package com.vsa.ecommerce.feature.order.get_order;

import com.vsa.ecommerce.common.abstraction.Request;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetOrderRequest implements Request {
    private Long orderId;
}
