package com.vsa.ecommerce.feature.cart.update_cart_item;

import com.vsa.ecommerce.common.abstraction.BaseController;
import com.vsa.ecommerce.feature.cart.dto.CartDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart")
public class UpdateCartItemController extends BaseController {

    private final UpdateCartItemService service;

    @PutMapping("/items/{itemId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartDto> updateCartItem(
            @PathVariable Long itemId,
            @RequestBody @Valid UpdateCartItemRequest request) {
        request.setItemId(itemId);
        return ResponseEntity.ok(service.execute(request));
    }
}
