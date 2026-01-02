package com.vsa.ecommerce.feature.order.list_orders;

import com.vsa.ecommerce.common.abstraction.Response;
import com.vsa.ecommerce.feature.order.dto.OrderDto;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ListOrdersResponse implements Response {
    private List<OrderDto> orders;
}
