package com.vsa.ecommerce.feature.inventory.adjust_inventory;

import com.vsa.ecommerce.common.abstraction.Request;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdjustInventoryRequest implements Request {
    private Long inventoryId;
    private int quantityChange; // Positive to add, negative to remove
    private String reason;
}
