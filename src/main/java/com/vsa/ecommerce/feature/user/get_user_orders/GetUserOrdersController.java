package com.vsa.ecommerce.feature.user.get_user_orders;

import com.vsa.ecommerce.common.abstraction.BaseController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User management APIs")
public class GetUserOrdersController extends BaseController {

    private final GetUserOrdersService getUserOrdersService;

    @GetMapping("/{id}/orders")
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isCurrentUser(#id)")
    public ResponseEntity<OrderListResponse> getUserOrders(@PathVariable Long id) {
        return ResponseEntity
                .ok(getUserOrdersService.execute(new com.vsa.ecommerce.feature.user.dto.UserIdRequest(id)));
    }
}
