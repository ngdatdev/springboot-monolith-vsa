package com.vsa.ecommerce.feature.product.get_product;

import com.vsa.ecommerce.common.abstraction.BaseController;
import com.vsa.ecommerce.feature.product.dto.ProductDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product")
public class GetProductController extends BaseController {

    private final GetProductService service;

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(service.execute(GetProductRequest.builder().productId(id).build()));
    }
}
