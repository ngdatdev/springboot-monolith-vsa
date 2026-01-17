package com.vsa.ecommerce.feature.product.list_products;

import com.vsa.ecommerce.common.abstraction.IService;
import com.vsa.ecommerce.domain.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ListProductsService implements IService<ListProductsRequest, ListProductsResponse> {

    private final ListProductsRepository repository;

    @Override
    @Transactional(readOnly = true)
    public ListProductsResponse execute(ListProductsRequest request) {
        List<Product> products = repository.findAll(request.getSearch(), request.getPage(), request.getSize());
        long total = repository.count(request.getSearch());

        List<ProductItem> items = products.stream().map(this::mapToItem).collect(Collectors.toList());
        return ListProductsResponse.builder().products(items).totalElements(total).build();
    }

    private ProductItem mapToItem(Product product) {
        return ProductItem.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .sku(product.getSku())
                .build();
    }
}
