package com.vsa.ecommerce.feature.order.admin_list_orders;

import com.vsa.ecommerce.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Order item for admin list orders response.
 * Each feature has its own response structure.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderItem {
    private Long id;
    private Long userId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private List<AdminOrderItemDetail> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminOrderItemDetail {
        private Long id;
        private Long productId;
        private String productName;
        private BigDecimal pricePerUnit;
        private Integer quantity;
        private BigDecimal totalPrice;
    }
}
