package com.vsa.ecommerce.feature.product.update_product;

import com.vsa.ecommerce.common.abstraction.IService;
import com.vsa.ecommerce.common.redis.KeyConvention;
import com.vsa.ecommerce.common.cache.hybrid.HybridCacheService;
import com.vsa.ecommerce.common.exception.BusinessException;
import com.vsa.ecommerce.common.exception.BusinessStatus;
import com.vsa.ecommerce.domain.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UpdateProductService implements IService<UpdateProductRequest, UpdateProductResponse> {

    private final UpdateProductRepository repository;
    private final HybridCacheService cacheService;
    private final KeyConvention keyConvention;

    @Override
    @Transactional
    public UpdateProductResponse execute(UpdateProductRequest request) {
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

        // Evict specific product cache
        cacheService.evict(keyConvention.buildKey(KeyConvention.RESOURCE_PRODUCT, product.getId().toString()));
        // Evict list patterns if they might be affected
        cacheService.evictPattern(keyConvention.buildPattern(KeyConvention.RESOURCE_PRODUCT));

        return UpdateProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .sku(product.getSku())
                .build();
    }
}
