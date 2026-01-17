package com.vsa.ecommerce.feature.order.get_order;

import com.vsa.ecommerce.common.abstraction.Response;
import com.vsa.ecommerce.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetOrderResponse implements Response {
    private Long id;
    private Long userId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private List<OrderItemInfo> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemInfo {
        private Long id;
        private Long productId;
        private String productName;
        private BigDecimal pricePerUnit;
        private Integer quantity;
        private BigDecimal totalPrice;
    }
}
