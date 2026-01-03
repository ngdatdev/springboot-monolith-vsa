package com.vsa.ecommerce.feature.inventory.adjust_inventory;

import com.vsa.ecommerce.common.abstraction.EmptyResponse;
import com.vsa.ecommerce.common.abstraction.IService;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.domain.entity.Inventory;
import com.vsa.ecommerce.domain.entity.InventoryTransaction;
import com.vsa.ecommerce.domain.enums.InventoryTransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdjustInventoryService implements IService<AdjustInventoryRequest, EmptyResponse> {

    private final AdjustInventoryRepository repository;

    @Override
    @Transactional
    public EmptyResponse execute(AdjustInventoryRequest request) {
        Inventory inventory = repository.findById(request.getInventoryId())
                .orElseThrow(() -> new BusinessException(BusinessStatus.NOT_FOUND));

        int oldQuantity = inventory.getAvailableQuantity();
        int newQuantity = oldQuantity + request.getQuantityChange();

        if (newQuantity < 0) {
            throw new BusinessException(BusinessStatus.INSUFFICIENT_STOCK); // Or custom error
        }

        inventory.setAvailableQuantity(newQuantity);
        // reservedQuantity typically stays same for manual adjustment

        repository.save(inventory);

        // Audit Log
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setInventory(inventory);
        transaction.setType(InventoryTransactionType.STOCK_ADJUSTMENT);
        transaction.setQuantityChange(request.getQuantityChange());
        transaction.setQuantityBefore(oldQuantity);
        transaction.setQuantityAfter(newQuantity);
        // reason is missing in InventoryTransaction entity? Let's check.
        // Based on previous view, it has `reason`? No, I saw "reason" in DTO but needed
        // to check entity.
        // Looking at file view Step 872: No `reason` field in entity.
        // It has `type`, `quantityChange`, `quantityBefore`, `quantityAfter`.
        // So I cannot save reason. I should probably add `reason` to entity if needed,
        // but for now I stick to entity definition.

        repository.saveTransaction(transaction);

        return new EmptyResponse();
    }
}
