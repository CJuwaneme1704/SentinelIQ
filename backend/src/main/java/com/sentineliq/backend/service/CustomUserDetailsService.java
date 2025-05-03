package com.sentineliq.backend.service;

import com.sentineliq.backend.model.User;
import com.sentineliq.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of the Spring Security `UserDetailsService` interface.
 * This service is used to load user-specific data during authentication.
 */
@RequiredArgsConstructor
@Service // Marks this class as a Spring-managed service component
public class CustomUserDetailsService implements UserDetailsService {

    
    private final UserRepository userRepository; // Injects the UserRepository to interact with the database

    /**
     * Loads a user by their email address.
     * This method is called by Spring Security during the authentication process.
     *
     * @param username The username of the user to load
     * @return A `UserDetails` object containing the user's information
     * @throws UsernameNotFoundException If no user is found with the given email
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch the user from the database using the email
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Return a Spring Security `User` object with the user's username, password, and authorities
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(), // Username 
            user.getPassword(), // Password
            new java.util.ArrayList<>() // Authorities (empty list for now, can be updated for roles)
        );
    }
}
