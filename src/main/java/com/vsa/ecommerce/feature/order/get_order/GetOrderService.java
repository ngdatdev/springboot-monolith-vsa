package com.vsa.ecommerce.feature.order.get_order;

import com.vsa.ecommerce.common.abstraction.Service;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.domain.entity.Order;
import com.vsa.ecommerce.feature.order.dto.OrderDto;
import com.vsa.ecommerce.feature.order.dto.OrderItemDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetOrderService implements Service<GetOrderRequest, OrderDto> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public OrderDto execute(GetOrderRequest request) {
        Order order = entityManager.find(Order.class, request.getOrderId());
        if (order == null) {
            throw new BusinessException(BusinessStatus.ORDER_NOT_FOUND);
        }

        return mapToDto(order);
    }

    private OrderDto mapToDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(order.getItems().stream().map(item -> OrderItemDto.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProductNameSnapshot())
                        .pricePerUnit(item.getPricePerUnitSnapshot())
                        .quantity(item.getQuantity())
                        .totalPrice(item.getPricePerUnitSnapshot()
                                .multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
                        .build()).collect(Collectors.toList()))
                .build();
    }
}
