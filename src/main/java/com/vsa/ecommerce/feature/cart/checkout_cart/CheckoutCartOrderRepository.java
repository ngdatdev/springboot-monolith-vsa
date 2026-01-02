package com.vsa.ecommerce.feature.cart.checkout_cart;

import com.vsa.ecommerce.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckoutCartOrderRepository extends JpaRepository<Order, Long> {
}
