package com.vsa.ecommerce.feature.order.list_orders;

import com.vsa.ecommerce.common.abstraction.Request;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListOrdersRequest implements Request {
    private int page;
    private int size;
}
