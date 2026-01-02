package com.vsa.ecommerce.feature.inventory.get_inventory_transactions;

import com.vsa.ecommerce.common.abstraction.Service;
import com.vsa.ecommerce.domain.entity.InventoryTransaction;
import com.vsa.ecommerce.feature.inventory.dto.InventoryTransactionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetInventoryTransactionsService
        implements Service<GetInventoryTransactionsRequest, GetInventoryTransactionsResponse> {

    private final GetInventoryTransactionsRepository repository;

    @Override
    @Transactional(readOnly = true)
    public GetInventoryTransactionsResponse execute(GetInventoryTransactionsRequest request) {
        List<InventoryTransaction> transactions = repository.findByInventoryId(request.getInventoryId());

        List<InventoryTransactionDto> dtos = transactions.stream().map(this::mapToDto).collect(Collectors.toList());
        return GetInventoryTransactionsResponse.builder().transactions(dtos).build();
    }

    private InventoryTransactionDto mapToDto(InventoryTransaction transaction) {
        return InventoryTransactionDto.builder()
                .id(transaction.getId())
                .inventoryId(transaction.getInventory().getId())
                .type(transaction.getType())
                .quantityChange(transaction.getQuantityChange())
                .reason(transaction.getReason())
                .referenceId(transaction.getReferenceId())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
