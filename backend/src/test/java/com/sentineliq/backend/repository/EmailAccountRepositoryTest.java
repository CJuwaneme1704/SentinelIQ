package com.sentineliq.backend.repository; // Package declaration

import com.sentineliq.backend.model.EmailAccount; // Import EmailAccount entity
import com.sentineliq.backend.model.User; // Import User entity
import org.junit.jupiter.api.BeforeEach; // Import JUnit BeforeEach annotation
import org.junit.jupiter.api.Test; // Import JUnit Test annotation
import org.springframework.beans.factory.annotation.Autowired; // Import Spring Autowired annotation
import org.springframework.boot.test.context.SpringBootTest; // Import SpringBootTest annotation
import org.springframework.transaction.annotation.Transactional; // Import Transactional annotation

import java.time.LocalDateTime; // Import LocalDateTime for date/time fields

import static org.junit.jupiter.api.Assertions.assertTrue; // Import assertion for true

@SpringBootTest // Enable Spring Boot test context
@Transactional // Roll back DB changes after each test
public class EmailAccountRepositoryTest { // Define the test class

    @Autowired
    private EmailAccountRepository emailAccountRepository; // Inject EmailAccountRepository bean

    @Autowired
    private UserRepository userRepository; // Inject UserRepository bean

    private User testUser; // Field to hold a test user

    @BeforeEach
    void setUp() { // Setup method to run before each test
        // Create and save a test user
        testUser = User.builder()
                .username("testuser") // Set username
                .name("Test User") // Set name
                .email("testuser@example.com") // Set email
                .password("password123") // Set password
                .role("USER") // Set role
                .createdAt(LocalDateTime.now()) // Set creation time
                .build(); // Build the User object

        userRepository.save(testUser); // Save the test user
    }

    /**
     * Test existence check for EmailAccount by email address.
     */
    @Test
    void testExistsByEmailAddress() {
        // Create and save an email account linked to the test user
        EmailAccount emailAccount = EmailAccount.builder()
                .user(testUser) // Associate the user
                .emailAddress("test@example.com") // Set email address
                .provider("Gmail") // Set provider
                .accessToken("access_token") // Set access token
                .refreshToken("refresh_token") // Set refresh token
                .lastSynced(LocalDateTime.now()) // Set last synced time
                .build(); // Build the EmailAccount object

        emailAccountRepository.save(emailAccount); // Save the email account

        // Verify the email account exists
        assertTrue(emailAccountRepository.existsByEmailAddress("test@example.com")); // Assert existence by email address
    }
}
