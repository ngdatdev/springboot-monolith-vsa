package com.vsa.ecommerce.feature.cart.get_cart;

import com.vsa.ecommerce.common.abstraction.Service;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.common.security.SecurityUtils;
import com.vsa.ecommerce.domain.entity.Cart;
import com.vsa.ecommerce.domain.entity.CartItem;
import com.vsa.ecommerce.domain.enums.CartStatus;
import com.vsa.ecommerce.feature.cart.dto.CartDto;
import com.vsa.ecommerce.feature.cart.dto.CartItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class GetCartService implements Service<GetCartRequest, CartDto> {

    private final GetCartRepository repository;

    @Override
    @Transactional(readOnly = true)
    public CartDto execute(GetCartRequest request) {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(BusinessStatus.UNAUTHORIZED));

        Cart cart = repository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElse(null);

        if (cart == null) {
            return CartDto.builder()
                    .items(Collections.emptyList())
                    .totalAmount(BigDecimal.ZERO)
                    .build();
        }

        return mapToDto(cart);
    }

    private CartDto mapToDto(Cart cart) {
        return CartDto.builder()
                .id(cart.getId())
                .totalAmount(cart.getTotalAmount())
                .status(cart.getStatus())
                .items(cart.getItems().stream().map(this::mapItemToDto).collect(Collectors.toList()))
                .build();
    }

    private CartItemDto mapItemToDto(CartItem item) {
        return CartItemDto.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .price(item.getPriceSnapshot())
                .subtotal(item.getPriceSnapshot().multiply(BigDecimal.valueOf(item.getQuantity())))
                .build();
    }
}
