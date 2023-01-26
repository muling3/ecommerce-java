package com.muling3.ecommerce.repositories;

import com.muling3.ecommerce.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
