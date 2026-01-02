package com.vsa.ecommerce.feature.user.get_user_orders;

import com.vsa.ecommerce.common.abstraction.Service;
import com.vsa.ecommerce.domain.entity.Order;
import com.vsa.ecommerce.feature.user.dto.UserIdRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetUserOrdersService implements Service<UserIdRequest, OrderListResponse> {

    private final GetUserOrdersRepository getUserOrdersRepository;

    @Override
    @Transactional(readOnly = true)
    public OrderListResponse execute(UserIdRequest request) {
        List<Order> orders = getUserOrdersRepository.findOrdersByUserId(request.getId());
        return OrderListResponse.builder().orders(orders).build();
    }
}
