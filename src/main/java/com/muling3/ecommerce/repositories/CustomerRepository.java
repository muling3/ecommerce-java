package com.muling3.ecommerce.repositories;

import com.muling3.ecommerce.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUserName(String name);

    Optional<Customer> findByEmail(String email);
}
