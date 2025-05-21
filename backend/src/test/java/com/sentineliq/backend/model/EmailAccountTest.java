package com.sentineliq.backend.model; // Package declaration

import org.junit.jupiter.api.Test; // Import JUnit 5 Test annotation
import java.util.Collections; // Import Collections utility class
import java.util.List; // Import List interface

import static org.junit.jupiter.api.Assertions.*; // Import all static assertion methods

class EmailAccountTest { // Define the test class

    /**
     * Test creation of EmailAccount with empty emails list and basic fields.
     */
    @Test
    void testEmailAccountCreation() { // Test EmailAccount creation with empty emails list
        EmailAccount emailAccount = new EmailAccount(); // Create new EmailAccount instance
        emailAccount.setEmailAddress("inbox@example.com"); // Set email address
        emailAccount.setProvider("Gmail"); // Set provider
        emailAccount.setEmails(Collections.emptyList()); // Set emails to empty list

        assertEquals("inbox@example.com", emailAccount.getEmailAddress()); // Assert email address is correct
        assertEquals("Gmail", emailAccount.getProvider()); // Assert provider is correct
        assertNotNull(emailAccount.getEmails()); // Assert emails list is not null
        assertEquals(0, emailAccount.getEmails().size()); // Assert emails list is empty
    }

    /**
     * Test EmailAccount with a list of Email objects.
     */
    @Test
    void testEmailAccountWithEmails() { // Test EmailAccount with two Email objects
        Email email1 = Email.builder() // Build first Email object
                .subject("Welcome") // Set subject
                .sender("welcome@example.com") // Set sender
                .body("Welcome to our service!") // Set body
                .trustScore(80) // Set trust score
                .build(); // Build the Email object

        Email email2 = Email.builder() // Build second Email object
                .subject("Reminder") // Set subject
                .sender("reminder@example.com") // Set sender
                .body("Don't forget your appointment") // Set body
                .trustScore(60) // Set trust score
                .build(); // Build the Email object

        EmailAccount emailAccount = new EmailAccount(); // Create new EmailAccount instance
        emailAccount.setEmailAddress("inbox@example.com"); // Set email address
        emailAccount.setProvider("Gmail"); // Set provider
        emailAccount.setEmails(List.of(email1, email2)); // Set emails list with two emails

        assertEquals(2, emailAccount.getEmails().size()); // Assert emails list size is 2
        assertEquals("Welcome", emailAccount.getEmails().get(0).getSubject()); // Assert first email subject
        assertEquals("Reminder", emailAccount.getEmails().get(1).getSubject()); // Assert second email subject
    }

    /**
     * Test EmailAccount with all fields set to null.
     */
    @Test
    void testEmailAccountWithNullValues() { // Test EmailAccount with null values
        EmailAccount emailAccount = new EmailAccount(); // Create new EmailAccount instance
        emailAccount.setEmailAddress(null); // Set email address to null
        emailAccount.setProvider(null); // Set provider to null
        emailAccount.setEmails(null); // Set emails list to null

        assertNull(emailAccount.getEmailAddress()); // Assert email address is null
        assertNull(emailAccount.getProvider()); // Assert provider is null
        assertNull(emailAccount.getEmails()); // Assert emails list is null
    }

    /**
     * Test EmailAccount with an explicitly empty emails list.
     */
    @Test
    void testEmailAccountWithEmptyEmailsList() { // Test EmailAccount with empty emails list
        EmailAccount emailAccount = new EmailAccount(); // Create new EmailAccount instance
        emailAccount.setEmailAddress("empty@example.com"); // Set email address
        emailAccount.setProvider("Gmail"); // Set provider
        emailAccount.setEmails(Collections.emptyList()); // Set emails to empty list

        assertNotNull(emailAccount.getEmails()); // Assert emails list is not null
        assertEquals(0, emailAccount.getEmails().size()); // Assert emails list is empty
    }

    /**
     * Test EmailAccount with a mix of Email objects and trust scores.
     */
    @Test
    void testEmailAccountWithMixedEmails() { // Test EmailAccount with mixed emails and trust scores
        Email email1 = Email.builder() // Build first Email object
                .subject("Test 1") // Set subject
                .sender("test1@example.com") // Set sender
                .body("Body 1") // Set body
                .trustScore(50) // Set trust score
                .build(); // Build the Email object

        Email email2 = Email.builder() // Build second Email object
                .subject("Test 2") // Set subject
                .sender("test2@example.com") // Set sender
                .body("Body 2") // Set body
                .trustScore(100) // Set trust score
                .build(); // Build the Email object

        EmailAccount emailAccount = new EmailAccount(); // Create new EmailAccount instance
        emailAccount.setEmailAddress("mixed@example.com"); // Set email address
        emailAccount.setProvider("Outlook"); // Set provider
        emailAccount.setEmails(List.of(email1, email2)); // Set emails list with two emails

        assertEquals(2, emailAccount.getEmails().size()); // Assert emails list size is 2
        assertEquals("Test 1", emailAccount.getEmails().get(0).getSubject()); // Assert first email subject
        assertEquals("Test 2", emailAccount.getEmails().get(1).getSubject()); // Assert second email subject
        assertEquals(50, emailAccount.getEmails().get(0).getTrustScore()); // Assert first email trust score
        assertEquals(100, emailAccount.getEmails().get(1).getTrustScore()); // Assert second email trust score
    }

    /**
     * Test EmailAccount with duplicate Email objects in the list.
     */
    @Test
    void testEmailAccountWithDuplicateEmails() { // Test EmailAccount with duplicate emails in the list
        Email email = Email.builder() // Build a single Email object
                .subject("Duplicate Email") // Set subject
                .sender("duplicate@example.com") // Set sender
                .body("This is a duplicate email") // Set body
                .trustScore(90) // Set trust score
                .build(); // Build the Email object

        EmailAccount emailAccount = new EmailAccount(); // Create new EmailAccount instance
        emailAccount.setEmailAddress("duplicates@example.com"); // Set email address
        emailAccount.setProvider("Yahoo"); // Set provider
        emailAccount.setEmails(List.of(email, email)); // Set emails list with duplicate emails

        assertEquals(2, emailAccount.getEmails().size()); // Assert emails list size is 2
        assertEquals("Duplicate Email", emailAccount.getEmails().get(0).getSubject()); // Assert first email subject
        assertEquals("Duplicate Email", emailAccount.getEmails().get(1).getSubject()); // Assert second email subject
    }
}
