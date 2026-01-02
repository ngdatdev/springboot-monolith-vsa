package com.vsa.ecommerce.feature.inventory.get_inventory_transactions;

import com.vsa.ecommerce.common.abstraction.Request;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetInventoryTransactionsRequest implements Request {
    private Long inventoryId;
}
