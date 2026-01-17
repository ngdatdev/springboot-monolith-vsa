package com.vsa.ecommerce.feature.product.list_products;

import com.vsa.ecommerce.common.abstraction.Response;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ListProductsResponse implements Response {
    private List<ProductItem> products;
    private long totalElements;
}
