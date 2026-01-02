package com.vsa.ecommerce.feature.order.admin_ship_order;

import com.vsa.ecommerce.common.abstraction.EmptyResponse;
import com.vsa.ecommerce.common.abstraction.Service;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.domain.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminShipOrderService implements Service<AdminShipOrderRequest, EmptyResponse> {

    private final AdminShipOrderRepository repository;

    @Override
    @Transactional
    public EmptyResponse execute(AdminShipOrderRequest request) {
        Order order = repository.findById(request.getOrderId())
                .orElseThrow(() -> new BusinessException(BusinessStatus.ORDER_NOT_FOUND));

        order.markShipped(request.getTrackingNumber(), request.getCarrier());
        repository.save(order);

        return new EmptyResponse();
    }
}
