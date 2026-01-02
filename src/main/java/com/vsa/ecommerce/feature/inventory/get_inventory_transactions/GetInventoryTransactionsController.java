package com.vsa.ecommerce.feature.inventory.get_inventory_transactions;

import com.vsa.ecommerce.common.abstraction.BaseController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory")
public class GetInventoryTransactionsController extends BaseController {

    private final GetInventoryTransactionsService service;

    @GetMapping("/{id}/transactions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GetInventoryTransactionsResponse> getTransactions(@PathVariable Long id) {
        return ResponseEntity.ok(service.execute(GetInventoryTransactionsRequest.builder().inventoryId(id).build()));
    }
}
