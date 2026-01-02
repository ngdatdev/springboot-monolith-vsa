package com.vsa.ecommerce.feature.cart.checkout_cart;

import com.vsa.ecommerce.common.abstraction.BaseController;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Cart")
public class CheckoutCartController extends BaseController {

    private final CheckoutCartService service;

    @PostMapping("/checkout")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CheckoutCartResponse> checkoutCart(
            @RequestBody(required = false) CheckoutCartRequest request) {
        if (request == null)
            request = new CheckoutCartRequest();
        return ResponseEntity.ok(service.execute(request));
    }
}
