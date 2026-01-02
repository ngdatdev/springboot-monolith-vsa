package com.vsa.ecommerce.feature.product.list_products;

import com.vsa.ecommerce.common.abstraction.Request;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListProductsRequest implements Request {
    private int page;
    private int size;
    private String search; // Optional search term
}
