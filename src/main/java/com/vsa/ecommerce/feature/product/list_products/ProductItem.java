package com.vsa.ecommerce.feature.product.list_products;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Product item DTO for list products response.
 * Each feature has its own response structure.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductItem {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String sku;
}
