package com.vsa.ecommerce.feature.user.get_user_orders;

import com.vsa.ecommerce.common.abstraction.Response;
import com.vsa.ecommerce.domain.entity.Order;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class OrderListResponse implements Response {
    private List<Order> orders;
}
