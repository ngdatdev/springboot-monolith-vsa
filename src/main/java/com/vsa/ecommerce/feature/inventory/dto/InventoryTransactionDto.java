package com.vsa.ecommerce.feature.inventory.dto;

import com.vsa.ecommerce.common.abstraction.Response;
import com.vsa.ecommerce.domain.enums.InventoryTransactionType;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class InventoryTransactionDto implements Response {
    private Long id;
    private Long inventoryId;
    private InventoryTransactionType type;
    private Integer quantityChange;
    private String reason;
    private String referenceId; // e.g. Order ID
    private LocalDateTime createdAt;
}
