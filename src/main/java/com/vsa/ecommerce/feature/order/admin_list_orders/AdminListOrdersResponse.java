package com.vsa.ecommerce.feature.order.admin_list_orders;

import com.vsa.ecommerce.common.abstraction.Response;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AdminListOrdersResponse implements Response {
    private List<AdminOrderItem> orders;
    private long totalElements; // Useful for admin pagination
}
