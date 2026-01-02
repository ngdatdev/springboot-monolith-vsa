package com.vsa.ecommerce.feature.order.list_orders;

import com.vsa.ecommerce.common.abstraction.BaseController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "Order Management APIs")
public class ListOrdersController extends BaseController {

    private final ListOrdersService listOrdersService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ListOrdersResponse> listMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(listOrdersService.execute(ListOrdersRequest.builder().page(page).size(size).build()));
    }
}
