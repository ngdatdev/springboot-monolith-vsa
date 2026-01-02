package com.vsa.ecommerce.feature.cart.checkout_cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.vsa.ecommerce.common.abstraction.Response;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutCartResponse implements Response {
    private String orderId;
}
