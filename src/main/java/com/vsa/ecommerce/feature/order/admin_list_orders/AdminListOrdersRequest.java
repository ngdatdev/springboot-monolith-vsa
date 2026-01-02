package com.vsa.ecommerce.feature.order.admin_list_orders;

import com.vsa.ecommerce.common.abstraction.Request;
import com.vsa.ecommerce.domain.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminListOrdersRequest implements Request {
    private int page;
    private int size;
    private OrderStatus status; // Optional filter
}
