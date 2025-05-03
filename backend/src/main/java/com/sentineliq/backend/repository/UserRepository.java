package com.sentineliq.backend.repository;

import com.sentineliq.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
   

    // ✅ Check by username
    boolean existsByUsername(String username);


    // ✅ Find by username (if logging in with username)
    Optional<User> findByUsername(String username);
}
