package com.sentineliq.backend.model; // Package declaration

import org.junit.jupiter.api.Test; // Import JUnit 5 Test annotation
import java.time.LocalDateTime; // Import LocalDateTime for date/time fields
import java.util.Collections; // Import Collections utility class

import static org.junit.jupiter.api.Assertions.*; // Import all static assertion methods

class UserTest { // Define the test class

    @Test // Mark this method as a test
    void testUserCreation() { // Test user creation and field values
        User user = User.builder() // Use builder pattern to create a User
                .email("testuser@example.com") // Set email
                .name("Test User") // Set name
                .password("securepassword") // Set password
                .username("testuser") // Set username
                .createdAt(LocalDateTime.now()) // Set creation time
                .build(); // Build the User object

        assertEquals("testuser@example.com", user.getEmail()); // Assert email is correct
        assertEquals("Test User", user.getName()); // Assert name is correct
        assertEquals("securepassword", user.getPassword()); // Assert password is correct
        assertEquals("testuser", user.getUsername()); // Assert username is correct
        assertEquals("USER", user.getRole()); // Assert default role is USER
        assertNotNull(user.getCreatedAt()); // Assert creation time is not null
    }

    @Test // Mark this method as a test
    void testUserWithEmailAccounts() { // Test user with email accounts list
        User user = User.builder() // Use builder pattern to create a User
                .email("testuser@example.com") // Set email
                .name("Test User") // Set name
                .password("securepassword") // Set password
                .username("testuser") // Set username
                .emailAccounts(Collections.emptyList()) // Set email accounts to empty list
                .build(); // Build the User object

        assertEquals(0, user.getEmailAccounts().size()); // Assert email accounts list is empty
    }
}
