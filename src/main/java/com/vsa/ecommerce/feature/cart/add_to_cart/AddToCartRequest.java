package com.vsa.ecommerce.feature.cart.add_to_cart;

import com.vsa.ecommerce.common.abstraction.Request;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartRequest implements Request {
    @NotNull
    private Long productId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
