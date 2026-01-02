package com.vsa.ecommerce.feature.product.get_product;

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
public class GetProductService implements Service<GetProductRequest, ProductDto> {

    private final GetProductRepository repository;

    @Override
    @Transactional(readOnly = true)
    public ProductDto execute(GetProductRequest request) {
        Product product = repository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException(BusinessStatus.PRODUCT_NOT_FOUND));

        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .sku(product.getSku())
                .build();
    }
}
