package com.vsa.ecommerce.feature.cart.update_cart_item;

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
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class UpdateCartItemService implements Service<UpdateCartItemRequest, CartDto> {

    private final UpdateCartItemRepository repository;

    @Override
    @Transactional
    public CartDto execute(UpdateCartItemRequest request) {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(BusinessStatus.UNAUTHORIZED));

        Cart cart = repository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(BusinessStatus.RESOURCE_NOT_FOUND));

        CartItem itemToUpdate = repository.findCartItemById(request.getItemId())
                .orElseThrow(() -> new BusinessException(BusinessStatus.RESOURCE_NOT_FOUND));

        if (!itemToUpdate.getCart().getId().equals(cart.getId())) {
            throw new BusinessException(BusinessStatus.FORBIDDEN);
        }

        itemToUpdate.setQuantity(request.getQuantity());
        cart.recalculateTotal();
        Cart savedCart = repository.save(cart);

        return mapToDto(savedCart);
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
