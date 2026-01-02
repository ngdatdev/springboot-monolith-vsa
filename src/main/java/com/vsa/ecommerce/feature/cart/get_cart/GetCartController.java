package com.vsa.ecommerce.feature.cart.get_cart;

import com.vsa.ecommerce.common.abstraction.BaseController;
import com.vsa.ecommerce.feature.cart.dto.CartDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart")
public class GetCartController extends BaseController {

    private final GetCartService service;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartDto> getCart() {
        return ResponseEntity.ok(service.execute(new GetCartRequest()));
    }
}
