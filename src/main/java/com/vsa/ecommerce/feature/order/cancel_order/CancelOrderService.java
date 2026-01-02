package com.vsa.ecommerce.feature.order.cancel_order;

import com.vsa.ecommerce.common.abstraction.Service;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.common.security.SecurityUtils;
import com.vsa.ecommerce.domain.entity.Order;
import com.vsa.ecommerce.domain.entity.OrderItem;
import com.vsa.ecommerce.domain.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CancelOrderService implements Service<CancelOrderRequest, CancelOrderResponse> {

    private final CancelOrderRepository cancelOrderRepository;

    @Override
    @Transactional
    public CancelOrderResponse execute(CancelOrderRequest request) {
        // 1. Get Order
        Order order = cancelOrderRepository.findOrderById(request.getOrderId())
                .orElseThrow(() -> new BusinessException(BusinessStatus.NOT_FOUND));

        // 2. Authorization Check (Start with simple ownership check, can expand to
        // ROLE_ADMIN check)
        // If current user is not owner AND not admin, throw error.
        Long currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(BusinessStatus.UNAUTHORIZED));
        boolean isAdmin = SecurityUtils.hasRole(UserRole.ADMIN.name());

        if (!order.getUser().getId().equals(currentUserId) && !isAdmin) {
            throw new BusinessException(BusinessStatus.UNAUTHORIZED); // Or FORBIDDEN
        }

        // 3. Cancel Order (Validates state)
        order.cancel(request.getReason());

        // 4. Release Inventory
        for (OrderItem item : order.getItems()) {
            cancelOrderRepository.findInventoryByProductId(item.getProduct().getId()).ifPresent(inventory -> {
                inventory.release(item.getQuantity());
                cancelOrderRepository.saveInventory(inventory);
            });
        }

        // 5. Save Order
        cancelOrderRepository.saveOrder(order);

        // TODO: Trigger Refund if Paid

        return CancelOrderResponse.builder()
                .orderId(order.getId().toString())
                .status(order.getStatus().name())
                .build();
    }
}
