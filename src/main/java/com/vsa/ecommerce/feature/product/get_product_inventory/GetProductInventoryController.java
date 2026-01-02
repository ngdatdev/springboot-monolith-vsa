package com.vsa.ecommerce.feature.product.get_product_inventory;

import com.vsa.ecommerce.common.abstraction.BaseController;
import com.vsa.ecommerce.feature.product.dto.ProductInventoryDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product")
public class GetProductInventoryController extends BaseController {

    private final GetProductInventoryService service;

    @GetMapping("/{id}/inventory")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductInventoryDto> getProductInventory(@PathVariable Long id) {
        return ResponseEntity.ok(service.execute(GetProductInventoryRequest.builder().productId(id).build()));
    }
}
