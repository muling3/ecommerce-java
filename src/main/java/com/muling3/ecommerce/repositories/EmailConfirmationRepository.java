package com.muling3.ecommerce.repositories;

import com.muling3.ecommerce.models.EmailConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailConfirmationRepository extends JpaRepository<EmailConfirmation, Long> {
    Optional<EmailConfirmation> findByToken(String token);
}
