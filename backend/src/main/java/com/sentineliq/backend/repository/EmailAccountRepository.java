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

    /**
     * Checks if an email account exists by its address.
     * Useful for validating unique email addresses during account linking.
     *
     * @param emailAddress the email address to check
     * @return true if an email account with the given address exists, false otherwise
     */
    boolean existsByEmailAddress(String emailAddress);
}
