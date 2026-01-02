package com.vsa.ecommerce.feature.product.get_product_inventory;

import com.vsa.ecommerce.common.abstraction.Request;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetProductInventoryRequest implements Request {
    private Long productId;
}
