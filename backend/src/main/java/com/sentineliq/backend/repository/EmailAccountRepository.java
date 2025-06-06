package com.sentineliq.backend.repository;

import com.sentineliq.backend.model.EmailAccount;
import com.sentineliq.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    /**
     * Finds all inboxes linked to a specific user.
     *
     * @param user the User object
     * @return list of inboxes owned by the user
     */
    List<EmailAccount> findAllByUser(User user);

    /**
     * Finds a specific inbox by ID and ensures it belongs to the given user.
     *
     * @param id the inbox ID
     * @param userId the ID of the authenticated user
     * @return Optional containing the inbox if found and owned by the user
     */
    Optional<EmailAccount> findByIdAndUserId(Long id, Long userId);
}
