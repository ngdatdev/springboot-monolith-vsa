package com.vsa.ecommerce.feature.product.list_products;

import com.vsa.ecommerce.common.abstraction.BaseController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product", description = "Product Management APIs")
public class ListProductsController extends BaseController {

    private final ListProductsService service;

    @GetMapping
    public ResponseEntity<ListProductsResponse> listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(service.execute(
                ListProductsRequest.builder()
                        .page(page)
                        .size(size)
                        .search(search)
                        .build()));
    }
}
