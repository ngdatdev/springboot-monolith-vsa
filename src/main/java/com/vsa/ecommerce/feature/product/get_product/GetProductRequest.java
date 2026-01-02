package com.vsa.ecommerce.feature.product.get_product;

import com.vsa.ecommerce.common.abstraction.Request;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetProductRequest implements Request {
    private Long productId;
}
