package com.vsa.ecommerce.feature.cart.checkout_cart;

import com.vsa.ecommerce.common.abstraction.Service;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.common.security.SecurityUtils;
import com.vsa.ecommerce.domain.entity.*;
import com.vsa.ecommerce.domain.enums.CartStatus;
import com.vsa.ecommerce.domain.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class CheckoutCartService implements Service<CheckoutCartRequest, CheckoutCartResponse> {

    private final CheckoutCartRepository cartRepository;
    private final CheckoutCartOrderRepository orderRepository;
    private final CheckoutCartInventoryRepository inventoryRepository;

    @Override
    @Transactional
    public CheckoutCartResponse execute(CheckoutCartRequest request) {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(BusinessStatus.UNAUTHORIZED));

        Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(BusinessStatus.RESOURCE_NOT_FOUND));

        if (cart.getItems().isEmpty()) {
            throw new BusinessException(BusinessStatus.CART_EMPTY);
        }

        // Initialize Order
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus(OrderStatus.PENDING_PAYMENT);

        // Process Items
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            // Check Inventory
            Inventory inventory = inventoryRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new BusinessException(BusinessStatus.INSUFFICIENT_STOCK));

            if (inventory.getAvailableQuantity() < cartItem.getQuantity()) {
                throw new BusinessException(BusinessStatus.INSUFFICIENT_STOCK);
            }

            // Reserve Inventory
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() - cartItem.getQuantity());
            inventory.setReservedQuantity(inventory.getReservedQuantity() + cartItem.getQuantity());
            inventoryRepository.save(inventory);

            // Create Order Item
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setProductNameSnapshot(product.getName());
            orderItem.setPricePerUnitSnapshot(cartItem.getPriceSnapshot()); // Use snapshot from cart

            order.addItem(orderItem);
        }

        // Save Order
        order.recalculateTotal();
        Order savedOrder = orderRepository.save(order);

        // Update Cart Status
        cart.setStatus(CartStatus.CHECKED_OUT);
        cartRepository.save(cart);

        return new CheckoutCartResponse(savedOrder.getId().toString());
    }
}
