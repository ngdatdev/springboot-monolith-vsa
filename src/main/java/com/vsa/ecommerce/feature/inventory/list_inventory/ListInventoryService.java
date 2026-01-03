package com.vsa.ecommerce.feature.inventory.list_inventory;

import com.vsa.ecommerce.common.abstraction.IService;
import com.vsa.ecommerce.domain.entity.Inventory;
import com.vsa.ecommerce.feature.inventory.dto.InventoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ListInventoryService implements IService<ListInventoryRequest, ListInventoryResponse> {

    private final ListInventoryRepository repository;

    @Override
    @Transactional(readOnly = true)
    public ListInventoryResponse execute(ListInventoryRequest request) {
        List<Inventory> inventoryList = repository.findAll(request.getPage(), request.getSize());
        long total = repository.count();

        List<InventoryDto> dtos = inventoryList.stream().map(this::mapToDto).collect(Collectors.toList());
        return ListInventoryResponse.builder().inventoryList(dtos).totalElements(total).build();
    }

    private InventoryDto mapToDto(Inventory inventory) {
        return InventoryDto.builder()
                .id(inventory.getId())
                .productId(inventory.getProduct().getId())
                .productName(inventory.getProduct().getName())
                .sku(inventory.getProduct().getSku())
                .availableQuantity(inventory.getAvailableQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .totalQuantity(inventory.getAvailableQuantity() + inventory.getReservedQuantity())
                .version(inventory.getVersion())
                .build();
    }
}
