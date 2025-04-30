package com.sentineliq.backend.repository;

import com.sentineliq.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// This interface lets Spring Data JPA automatically create SQL queries for you
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Finds if an email already exists (no need to manually write SQL)
    boolean existsByEmail(String email);

     //Find a user by email
     Optional<User> findByEmail(String email);
}
