package com.vsa.ecommerce.feature.cart.remove_from_cart;

import com.vsa.ecommerce.common.abstraction.BaseController;
import com.vsa.ecommerce.feature.cart.dto.CartDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart")
public class RemoveFromCartController extends BaseController {

    private final RemoveFromCartService service;

    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartDto> removeFromCart(@PathVariable Long itemId) {
        return ResponseEntity.ok(service.execute(RemoveFromCartRequest.builder().itemId(itemId).build()));
    }
}
