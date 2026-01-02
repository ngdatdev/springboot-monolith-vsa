package com.vsa.ecommerce.feature.cart.get_cart;

import com.vsa.ecommerce.domain.entity.Cart;
import com.vsa.ecommerce.domain.enums.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("FeatureGetCartRepository")
public interface GetCartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserIdAndStatus(Long userId, CartStatus status);
}
