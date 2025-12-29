package com.vsa.ecommerce.feature.order.create_order;

import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.common.abstraction.Service;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class CreateOrderService implements Service<CreateOrderRequest, CreateOrderResponse> {

    @Override
    public CreateOrderResponse execute(CreateOrderRequest request) {
        // Validation logic
        if (request.getProductId() == null || request.getProductId().isEmpty()) {
            throw new BusinessException(BusinessStatus.INVALID_PRODUCT);
        }
        if (request.getQuantity() <= 0) {
            throw new BusinessException(BusinessStatus.INVALID_QUANTITY, request.getQuantity());
        }

        // Business logic
        // In a real scenario, we might return an Entity or DTO from repository
        // String orderId = orderRepository.saveOrder(request.getProductId(),
        // request.getQuantity());
        String orderId = UUID.randomUUID().toString(); // Mock ID

        return new CreateOrderResponse(orderId);
    }
}
