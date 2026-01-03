package com.vsa.ecommerce.feature.order.create_order;

import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.common.abstraction.IService;
import lombok.RequiredArgsConstructor;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional
public class CreateOrderService implements IService<CreateOrderRequest, CreateOrderResponse> {

    private final CreateOrderRepository createOrderRepository;

    @Override
    public CreateOrderResponse execute(CreateOrderRequest request) {
        // 1. Get Current User
        Long userId = com.vsa.ecommerce.common.security.SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(BusinessStatus.UNAUTHORIZED));

        com.vsa.ecommerce.domain.entity.User user = createOrderRepository.findUserById(userId)
                .orElseThrow(() -> new BusinessException(BusinessStatus.USER_NOT_FOUND));

        // 2. Initialize Order
        com.vsa.ecommerce.domain.entity.Order order = new com.vsa.ecommerce.domain.entity.Order();
        order.setUser(user);
        order.setStatus(com.vsa.ecommerce.domain.enums.OrderStatus.PENDING_PAYMENT);

        // 3. Process Items
        for (CreateOrderRequest.OrderItemDto itemDto : request.getItems()) {
            // Validate Product
            com.vsa.ecommerce.domain.entity.Product product = createOrderRepository
                    .findProductById(itemDto.getProductId())
                    .orElseThrow(() -> new BusinessException(BusinessStatus.PRODUCT_NOT_FOUND));

            // Check Inventory (Optimistic Locking via Version)
            com.vsa.ecommerce.domain.entity.Inventory inventory = createOrderRepository
                    .findInventoryByProductId(product.getId())
                    .orElseThrow(() -> new BusinessException(BusinessStatus.INSUFFICIENT_STOCK));

            if (inventory.getAvailableQuantity() < itemDto.getQuantity()) {
                throw new BusinessException(BusinessStatus.INSUFFICIENT_STOCK);
            }

            // Reserve Inventory
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() - itemDto.getQuantity());
            inventory.setReservedQuantity(inventory.getReservedQuantity() + itemDto.getQuantity());
            createOrderRepository.saveInventory(inventory); // Optimistic lock check happens here on flush/commit

            // Create Order Item
            com.vsa.ecommerce.domain.entity.OrderItem orderItem = new com.vsa.ecommerce.domain.entity.OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setProductNameSnapshot(product.getName());
            orderItem.setPricePerUnitSnapshot(product.getPrice());

            order.addItem(orderItem);
        }

        // 4. Save Order
        order.recalculateTotal();
        com.vsa.ecommerce.domain.entity.Order savedOrder = createOrderRepository.saveOrder(order);

        return new CreateOrderResponse(savedOrder.getId().toString());
    }
}
