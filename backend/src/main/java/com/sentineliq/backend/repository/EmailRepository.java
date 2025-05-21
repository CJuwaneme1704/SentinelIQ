package com.sentineliq.backend.repository;

import com.sentineliq.backend.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository for managing Email entities.
 * Provides CRUD access to stored email messages linked to inboxes.
 */
@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {

    /**
     * Finds all emails for a specific email account, sorted by the date they were received.
     * Useful for displaying an inbox view.
     *
     * @param emailAccountId the ID of the email account
     * @return a list of emails ordered by received date (most recent first)
     */
    List<Email> findByEmailAccountIdOrderByReceivedAtDesc(Long emailAccountId);
}
