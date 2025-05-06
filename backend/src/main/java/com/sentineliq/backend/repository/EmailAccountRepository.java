package com.sentineliq.backend.repository;

import com.sentineliq.backend.model.EmailAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing EmailAccount entities.
 * Provides CRUD access to linked email inboxes.
 */
@Repository
public interface EmailAccountRepository extends JpaRepository<EmailAccount, Long> {
    boolean existsByEmailAddress(String emailAddress);
}
