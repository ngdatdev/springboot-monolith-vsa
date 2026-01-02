package com.vsa.ecommerce.feature.product.list_products;

import com.vsa.ecommerce.common.abstraction.Service;
import com.vsa.ecommerce.domain.entity.Product;
import com.vsa.ecommerce.feature.product.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ListProductsService implements Service<ListProductsRequest, ListProductsResponse> {

    private final ListProductsRepository repository;

    @Override
    @Transactional(readOnly = true)
    public ListProductsResponse execute(ListProductsRequest request) {
        List<Product> products = repository.findAll(request.getSearch(), request.getPage(), request.getSize());
        long total = repository.count(request.getSearch());

        List<ProductDto> dtos = products.stream().map(this::mapToDto).collect(Collectors.toList());
        return ListProductsResponse.builder().products(dtos).totalElements(total).build();
    }

    private ProductDto mapToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .sku(product.getSku())
                .build();
    }
}
