package com.vsa.ecommerce.feature.order.admin_list_orders;

import com.vsa.ecommerce.common.abstraction.Response;
import com.vsa.ecommerce.feature.order.dto.OrderDto;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AdminListOrdersResponse implements Response {
    private List<OrderDto> orders;
    private long totalElements; // Useful for admin pagination
}
