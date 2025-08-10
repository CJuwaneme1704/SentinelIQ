package com.sentineliq.backend.repository; // Package declaration

import com.sentineliq.backend.model.Email; // Import Email entity
import com.sentineliq.backend.model.EmailAccount; // Import EmailAccount entity
import com.sentineliq.backend.model.User; // Import User entity
import org.junit.jupiter.api.BeforeEach; // Import JUnit BeforeEach annotation
import org.junit.jupiter.api.Test; // Import JUnit Test annotation
import org.springframework.beans.factory.annotation.Autowired; // Import Spring Autowired annotation
import org.springframework.boot.test.context.SpringBootTest; // Import SpringBootTest annotation
import org.springframework.transaction.annotation.Transactional; // Import Transactional annotation

import java.time.LocalDateTime; // Import LocalDateTime for date/time fields
import java.util.List; // Import List interface

import static org.junit.jupiter.api.Assertions.*; // Import all static assertion methods

@SpringBootTest // Enable Spring Boot test context
@Transactional // Roll back DB changes after each test
class EmailRepositoryTest { // Define the test class

    @Autowired
    private EmailRepository emailRepository; // Inject EmailRepository bean

    @Autowired
    private EmailAccountRepository emailAccountRepository; // Inject EmailAccountRepository bean

    @Autowired
    private UserRepository userRepository; // Inject UserRepository bean

    private EmailAccount testAccount; // Field to hold a test email account

    @BeforeEach
    void setUp() { // Setup method to run before each test
        // Create and save a test user
        User testUser = User.builder()
                .email("testuser@example.com") // Set email
                .name("Test User") // Set name
                .password("securepassword") // Set password
                .username("testuser") // Set username
                .role("USER") // Set role
                .createdAt(LocalDateTime.now()) // Set creation time
                .build(); // Build the User object

        userRepository.save(testUser); // Save the test user

        // Create and save an email account for the user
        testAccount = new EmailAccount(); // Create new EmailAccount instance
        testAccount.setEmailAddress("testaccount@example.com"); // Set email address
        testAccount.setProvider("Gmail"); // Set provider
        testAccount.setUser(testUser); // Associate with test user

        emailAccountRepository.save(testAccount); // Save the email account
    }

    /**
     * Test finding emails by account ID in descending order of received date.
     */
    @Test
    void testFindByEmailAccountIdOrderByReceivedAtDesc() { // Test fetching emails in descending order
        // Create and save emails
        Email email1 = new Email(); // Create first Email
        email1.setSubject("First Email"); // Set subject
        email1.setPlainTextBody("First email body"); // Set body
        email1.setSender("sender1@example.com"); // Set sender
        email1.setReceivedAt(LocalDateTime.now().minusDays(2)); // Set received date (older)
        email1.setEmailAccount(testAccount); // Associate with test account

        Email email2 = new Email(); // Create second Email
        email2.setSubject("Second Email"); // Set subject
        email2.setPlainTextBody("Second email body"); // Set body
        email2.setSender("sender2@example.com"); // Set sender
        email2.setReceivedAt(LocalDateTime.now()); // Set received date (newer)
        email2.setEmailAccount(testAccount); // Associate with test account

        emailRepository.save(email1); // Save first email
        emailRepository.save(email2); // Save second email

        // Fetch emails in descending order of received date
        List<Email> emails = emailRepository.findByEmailAccountIdOrderByReceivedAtDesc(testAccount.getId());

        // Verify the emails are in the correct order
        assertEquals(2, emails.size()); // Assert two emails are returned
        assertEquals("Second Email", emails.get(0).getSubject()); // Assert first is the newest
        assertEquals("First Email", emails.get(1).getSubject()); // Assert second is the oldest
    }
}
