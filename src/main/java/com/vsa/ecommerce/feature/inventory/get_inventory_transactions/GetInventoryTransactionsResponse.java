package com.vsa.ecommerce.feature.inventory.get_inventory_transactions;

import com.vsa.ecommerce.common.abstraction.Response;
import com.vsa.ecommerce.feature.inventory.dto.InventoryTransactionDto;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class GetInventoryTransactionsResponse implements Response {
    private List<InventoryTransactionDto> transactions;
}
