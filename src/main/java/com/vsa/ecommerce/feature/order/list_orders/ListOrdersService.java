package com.vsa.ecommerce.feature.order.list_orders;

import com.vsa.ecommerce.common.abstraction.IService;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.common.security.SecurityUtils;
import com.vsa.ecommerce.domain.entity.Order;
import com.vsa.ecommerce.feature.order.dto.OrderDto;
import com.vsa.ecommerce.feature.order.dto.OrderItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ListOrdersService implements IService<ListOrdersRequest, ListOrdersResponse> {

    private final ListOrdersRepository listOrdersRepository;

    @Override
    @Transactional(readOnly = true)
    public ListOrdersResponse execute(ListOrdersRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(BusinessStatus.UNAUTHORIZED));

        List<Order> orders = listOrdersRepository.findByUserId(currentUserId, request.getPage(), request.getSize());

        List<OrderDto> dtos = orders.stream().map(this::mapToDto).collect(Collectors.toList());
        return ListOrdersResponse.builder().orders(dtos).build();
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
