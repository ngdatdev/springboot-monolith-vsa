package com.vsa.ecommerce.feature.inventory.dto;

import com.vsa.ecommerce.common.abstraction.Response;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryDto implements Response {
    private Long id;
    private Long productId;
    private String productName;
    private String sku;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private Integer totalQuantity;
    private Long version;
}
