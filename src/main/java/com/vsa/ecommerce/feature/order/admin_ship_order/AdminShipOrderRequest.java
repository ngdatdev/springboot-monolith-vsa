package com.vsa.ecommerce.feature.order.admin_ship_order;

import com.vsa.ecommerce.common.abstraction.Request;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminShipOrderRequest implements Request {
    private Long orderId;
    private String trackingNumber;
    private String carrier;
}
