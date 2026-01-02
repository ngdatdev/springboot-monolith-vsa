package com.vsa.ecommerce.feature.inventory.list_inventory;

import com.vsa.ecommerce.common.abstraction.Request;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListInventoryRequest implements Request {
    private int page;
    private int size;
}
