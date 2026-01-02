package com.vsa.ecommerce.feature.product.update_product;

import com.vsa.ecommerce.common.abstraction.Service;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.domain.entity.Product;
import com.vsa.ecommerce.feature.product.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UpdateProductService implements Service<UpdateProductRequest, ProductDto> {

    private final UpdateProductRepository repository;

    @Override
    @Transactional
    public ProductDto execute(UpdateProductRequest request) {
        Product product = repository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException(BusinessStatus.PRODUCT_NOT_FOUND));

        if (request.getName() != null)
            product.setName(request.getName());
        if (request.getDescription() != null)
            product.setDescription(request.getDescription());
        if (request.getPrice() != null)
            product.setPrice(request.getPrice());
        if (request.getSku() != null)
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
