package com.vsa.ecommerce.feature.product.get_product_inventory;

import com.vsa.ecommerce.common.abstraction.IService;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.domain.entity.Inventory;
import com.vsa.ecommerce.feature.product.dto.ProductInventoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GetProductInventoryService implements IService<GetProductInventoryRequest, ProductInventoryDto> {

    private final GetProductInventoryRepository repository;

    @Override
    @Transactional(readOnly = true)
    public ProductInventoryDto execute(GetProductInventoryRequest request) {
        Inventory inventory = repository.findByProductId(request.getProductId())
                .orElseThrow(() -> new BusinessException(BusinessStatus.PRODUCT_NOT_FOUND)); // Or create specific
                                                                                             // INVENTORY_NOT_FOUND

        return ProductInventoryDto.builder()
                .productId(inventory.getProduct().getId())
                .productName(inventory.getProduct().getName())
                .sku(inventory.getProduct().getSku())
                .availableQuantity(inventory.getAvailableQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .totalQuantity(inventory.getAvailableQuantity() + inventory.getReservedQuantity())
                .build();
    }
}
