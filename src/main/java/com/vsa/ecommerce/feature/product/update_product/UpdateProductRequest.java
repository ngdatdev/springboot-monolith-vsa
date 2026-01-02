package com.vsa.ecommerce.feature.product.update_product;

import com.vsa.ecommerce.common.abstraction.Request;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest implements Request {
    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    private String sku;
}
