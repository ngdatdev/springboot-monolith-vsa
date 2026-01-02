package com.vsa.ecommerce.feature.product.create_product;

import com.vsa.ecommerce.common.abstraction.Service;
import com.vsa.ecommerce.domain.entity.Product;
import com.vsa.ecommerce.feature.product.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CreateProductService implements Service<CreateProductRequest, ProductDto> {

    private final CreateProductRepository repository;

    @Override
    @Transactional
    public ProductDto execute(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSku(request.getSku());

        repository.save(product);

        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .sku(product.getSku())
                .build();
    }
}
