package com.vsa.ecommerce.feature.user.get_user_orders;

import com.vsa.ecommerce.common.abstraction.IService;
import com.vsa.ecommerce.domain.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetUserOrdersService implements IService<GetUserOrdersRequest, GetOrderListResponse> {

    private final GetUserOrdersRepository getUserOrdersRepository;

    @Override
    @Transactional(readOnly = true)
    public GetOrderListResponse execute(GetUserOrdersRequest request) {
        List<Order> orders = getUserOrdersRepository.findOrdersByUserId(request.getUserId());
        return GetOrderListResponse.builder().orders(orders).build();
    }
}
