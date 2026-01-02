package com.vsa.ecommerce.feature.cart.checkout_cart;

import com.vsa.ecommerce.domain.entity.Cart;
import com.vsa.ecommerce.domain.enums.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("FeatureCheckoutCartRepository")
public interface CheckoutCartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserIdAndStatus(Long userId, CartStatus status);
}
