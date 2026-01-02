package com.vsa.ecommerce.feature.cart.add_to_cart;

import com.vsa.ecommerce.domain.entity.Cart;
import com.vsa.ecommerce.domain.entity.Product;
import com.vsa.ecommerce.domain.entity.User;
import com.vsa.ecommerce.domain.enums.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("FeatureAddToCartRepository")
public interface AddToCartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findUserById(@Param("id") Long id);

    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findProductById(@Param("id") Long id);

    Optional<Cart> findByUserIdAndStatus(Long userId, CartStatus status);
}
