package com.vsa.ecommerce.feature.cart.update_cart_item;

import com.vsa.ecommerce.domain.entity.Cart;
import com.vsa.ecommerce.domain.entity.CartItem;
import com.vsa.ecommerce.domain.enums.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("FeatureUpdateCartItemRepository")
public interface UpdateCartItemRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserIdAndStatus(Long userId, CartStatus status);

    @Query("SELECT ci FROM CartItem ci WHERE ci.id = :id")
    Optional<CartItem> findCartItemById(@Param("id") Long id);
}
