package com.vsa.ecommerce.feature.order.admin_list_orders;

import com.vsa.ecommerce.common.abstraction.BaseController;
import com.vsa.ecommerce.domain.enums.OrderStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@Tag(name = "Order Admin", description = "Admin Order Management APIs")
public class AdminListOrdersController extends BaseController {

    private final AdminListOrdersService adminListOrdersService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminListOrdersResponse> listOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) OrderStatus status) {
        return ResponseEntity.ok(adminListOrdersService.execute(
                AdminListOrdersRequest.builder()
                        .page(page)
                        .size(size)
                        .status(status)
                        .build()));
    }
}
