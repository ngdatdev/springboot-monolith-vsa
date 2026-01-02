package com.vsa.ecommerce.feature.inventory.list_inventory;

import com.vsa.ecommerce.common.abstraction.Response;
import com.vsa.ecommerce.feature.inventory.dto.InventoryDto;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ListInventoryResponse implements Response {
    private List<InventoryDto> inventoryList;
    private long totalElements;
}
