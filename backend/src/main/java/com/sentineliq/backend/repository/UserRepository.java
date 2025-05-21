package com.sentineliq.backend.repository;

import com.sentineliq.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository for managing User entities.
 * Handles database operations for registered users, including lookup by username and email.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Checks if a user exists by their username.
     * Useful for validating unique usernames during registration.
     *
     * @param username the username to check
     * @return true if a user with the given username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user exists by their email address.
     * Useful for validating unique emails during registration.
     *
     * @param email the email to check
     * @return true if a user with the given email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Finds a user by their username.
     * Used for login and authentication processes.
     *
     * @param username the username to search for
     * @return an Optional containing the user if found, empty otherwise
     */
    Optional<User> findByUsername(String username);
}
