package com.sentineliq.backend.repository; // Package declaration

import com.sentineliq.backend.model.User; // Import User entity
import com.sentineliq.backend.model.EmailAccount; // Import EmailAccount entity
import org.junit.jupiter.api.BeforeEach; // Import JUnit BeforeEach annotation
import org.junit.jupiter.api.Test; // Import JUnit Test annotation
import org.springframework.beans.factory.annotation.Autowired; // Import Spring Autowired annotation
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest; // Import Spring DataJpaTest annotation
import org.springframework.test.context.ActiveProfiles; // Import Spring ActiveProfiles annotation

import java.time.LocalDateTime; // Import LocalDateTime for date/time fields
import java.util.Optional; // Import Optional for optional return types

import static org.junit.jupiter.api.Assertions.*; // Import all static assertion methods

@DataJpaTest // Enable JPA tests with in-memory database
@ActiveProfiles("test") // Use the "test" profile for this test class
class UserRepositoryTest { // Define the test class

    @Autowired
    private UserRepository userRepository; // Inject UserRepository bean

    @Autowired
    private EmailAccountRepository emailAccountRepository; // Inject EmailAccountRepository bean

    private User testUser; // Field to hold a test user

    @BeforeEach
    void setUp() { // Setup method to run before each test
        // Clear previous data to prevent overlapping state between tests
        emailAccountRepository.deleteAll(); // Delete all email accounts
        userRepository.deleteAll(); // Delete all users

        // Create a fresh test user
        testUser = User.builder()
                .username("testuser") // Set username
                .email("testuser@example.com") // Set email
                .name("Test User") // Set name
                .password("securepassword") // Set password
                .createdAt(LocalDateTime.now()) // Set creation time
                .build(); // Build the User object

        userRepository.save(testUser); // Save the test user to the repository
    }

    /**
     * Test saving a user and finding by username.
     */
    @Test
    void testUserSaveAndFindByUsername() {
        Optional<User> foundUser = userRepository.findByUsername("testuser"); // Find user by username
        assertTrue(foundUser.isPresent()); // Assert user is found
        assertEquals("testuser@example.com", foundUser.get().getEmail()); // Assert email matches
    }

    /**
     * Test existence check by username.
     */
    @Test
    void testExistsByUsername() {
        assertTrue(userRepository.existsByUsername("testuser")); // Assert test user exists
        assertFalse(userRepository.existsByUsername("nonexistentuser")); // Assert nonexistent user does not exist
    }

    /**
     * Test existence check by email.
     */
    @Test
    void testExistsByEmail() {
        assertTrue(userRepository.existsByEmail("testuser@example.com")); // Assert test user email exists
        assertFalse(userRepository.existsByEmail("nonexistent@example.com")); // Assert nonexistent email does not exist
    }

    /**
     * Test associating a single EmailAccount with a user.
     */
    @Test
    void testEmailAccountAssociation() {
        // Use a unique email address to avoid conflicts
        String uniqueEmail = "unique-testaccount-" + System.currentTimeMillis() + "@example.com";

        // Create and associate an EmailAccount
        EmailAccount emailAccount = EmailAccount.builder()
                .user(testUser) // Set user
                .emailAddress(uniqueEmail) // Set email address
                .provider("Gmail") // Set provider
                .lastSynced(LocalDateTime.now()) // Set last synced time
                .accessToken("dummy-access-token") // Set access token
                .refreshToken("dummy-refresh-token") // Set refresh token
                .build(); // Build the EmailAccount object

        // Add the email account to the user and save
        testUser.getEmailAccounts().add(emailAccount); // Add email account to user
        userRepository.save(testUser); // Save user with email account

        // Fetch the user again to check the email association
        Optional<User> foundUser = userRepository.findByUsername("testuser"); // Find user by username
        assertTrue(foundUser.isPresent()); // Assert user is found

        // Ensure only one email account is associated
        assertEquals(1, foundUser.get().getEmailAccounts().size()); // Assert one email account is associated
        assertEquals(uniqueEmail, foundUser.get().getEmailAccounts().get(0).getEmailAddress()); // Assert email address matches
    }

    /**
     * Test associating multiple EmailAccounts with a user.
     */
    @Test
    void testMultipleEmailAccounts() {
        // Add two unique email accounts
        EmailAccount emailAccount1 = EmailAccount.builder()
                .user(testUser) // Set user
                .emailAddress("first-" + System.currentTimeMillis() + "@example.com") // Set first email address
                .provider("Gmail") // Set provider
                .lastSynced(LocalDateTime.now()) // Set last synced time
                .accessToken("token1") // Set access token
                .refreshToken("refresh1") // Set refresh token
                .build(); // Build the first EmailAccount object

        EmailAccount emailAccount2 = EmailAccount.builder()
                .user(testUser) // Set user
                .emailAddress("second-" + System.currentTimeMillis() + "@example.com") // Set second email address
                .provider("Outlook") // Set provider
                .lastSynced(LocalDateTime.now()) // Set last synced time
                .accessToken("token2") // Set access token
                .refreshToken("refresh2") // Set refresh token
                .build(); // Build the second EmailAccount object

        testUser.getEmailAccounts().add(emailAccount1); // Add first email account to user
        testUser.getEmailAccounts().add(emailAccount2); // Add second email account to user
        userRepository.save(testUser); // Save user with both email accounts

        // Verify both email accounts are associated
        Optional<User> foundUser = userRepository.findByUsername("testuser"); // Find user by username
        assertTrue(foundUser.isPresent()); // Assert user is found
        assertEquals(2, foundUser.get().getEmailAccounts().size()); // Assert two email accounts are associated
    }
}
