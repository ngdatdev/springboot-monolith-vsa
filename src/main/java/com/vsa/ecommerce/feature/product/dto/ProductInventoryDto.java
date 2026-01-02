package com.vsa.ecommerce.feature.product.dto;

import com.vsa.ecommerce.common.abstraction.Response;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductInventoryDto implements Response {
    private Long productId;
    private String productName;
    private String sku;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private Integer totalQuantity;
}
