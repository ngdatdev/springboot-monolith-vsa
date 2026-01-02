package com.vsa.ecommerce.feature.product.dto;

import com.vsa.ecommerce.common.abstraction.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto implements Response {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String sku;
}
