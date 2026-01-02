package com.vsa.ecommerce.feature.cart.add_to_cart;

import com.vsa.ecommerce.common.abstraction.BaseController;
import com.vsa.ecommerce.feature.cart.dto.CartDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Shopping Cart APIs")
public class AddToCartController extends BaseController {

    private final AddToCartService service;

    @PostMapping("/items")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartDto> addToCart(@RequestBody @Valid AddToCartRequest request) {
        return ResponseEntity.ok(service.execute(request));
    }
}
