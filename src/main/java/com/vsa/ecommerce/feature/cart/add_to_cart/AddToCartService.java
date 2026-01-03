package com.vsa.ecommerce.feature.cart.add_to_cart;

import com.vsa.ecommerce.common.abstraction.IService;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.common.security.SecurityUtils;
import com.vsa.ecommerce.domain.entity.Cart;
import com.vsa.ecommerce.domain.entity.CartItem;
import com.vsa.ecommerce.domain.entity.Product;
import com.vsa.ecommerce.domain.entity.User;
import com.vsa.ecommerce.domain.enums.CartStatus;
import com.vsa.ecommerce.feature.cart.dto.CartDto;
import com.vsa.ecommerce.feature.cart.dto.CartItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class AddToCartService implements IService<AddToCartRequest, CartDto> {

    private final AddToCartRepository repository;

    @Override
    @Transactional
    public CartDto execute(AddToCartRequest request) {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(BusinessStatus.UNAUTHORIZED));

        User user = repository.findUserById(userId)
                .orElseThrow(() -> new BusinessException(BusinessStatus.USER_NOT_FOUND));

        Product product = repository.findProductById(request.getProductId())
                .orElseThrow(() -> new BusinessException(BusinessStatus.PRODUCT_NOT_FOUND));

        Cart cart = repository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setStatus(CartStatus.ACTIVE);
                    return repository.save(newCart);
                });

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            // Update price snapshot to current price if logic dictates, or keep original?
            // Usually we update to current price on add
            item.setPriceSnapshot(product.getPrice());
        } else {
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            newItem.setPriceSnapshot(product.getPrice());
            cart.addItem(newItem);
        }

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
