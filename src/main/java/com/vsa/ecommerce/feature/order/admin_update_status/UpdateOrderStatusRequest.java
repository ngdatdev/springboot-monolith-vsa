package com.vsa.ecommerce.feature.order.admin_update_status;

import com.vsa.ecommerce.common.abstraction.Request;
import com.vsa.ecommerce.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest implements Request {
    private Long orderId;
    private OrderStatus newStatus;
}
