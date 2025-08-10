package com.sentineliq.backend.model; // Package declaration

import org.junit.jupiter.api.Test; // Import JUnit 5 Test annotation
import java.time.LocalDateTime; // Import LocalDateTime for date/time fields

import static org.junit.jupiter.api.Assertions.*; // Import all static assertion methods

class EmailTest { // Define the test class

    @Test // Mark this method as a test
    void testEmailCreationWithBuilder() { // Test email creation with builder
        Email email = Email.builder() // Use builder pattern to create an Email
                .subject("Test Subject") // Set subject
                .sender("sender@example.com") // Set sender
                .plainTextBody("Test Body") // Set body
                .receivedAt(LocalDateTime.now()) // Set received time
                .isSpam(false) // Set spam flag
                .trustScore(90) // Set trust score
                .build(); // Build the Email object

        assertEquals("Test Subject", email.getSubject()); // Assert subject is correct
        assertEquals("sender@example.com", email.getSender()); // Assert sender is correct
        assertEquals("Test Body", email.getPlainTextBody()); // Assert body is correct
        assertFalse(email.getIsSpam()); // Assert spam flag is false
        assertEquals(90, email.getTrustScore()); // Assert trust score is correct
        assertNotNull(email.getReceivedAt()); // Assert received time is not null
    }

    @Test // Mark this method as a test
    void testEmailWithNullValues() { // Test email creation with null values
        Email email = Email.builder() // Use builder pattern to create an Email
                .subject(null) // Set subject to null
                .sender(null) // Set sender to null
                .plainTextBody(null) // Set body to null
                .receivedAt(null) // Set received time to null
                .isSpam(null) // Set spam flag to null
                .trustScore(null) // Set trust score to null
                .build(); // Build the Email object

        assertNull(email.getSubject()); // Assert subject is null
        assertNull(email.getSender()); // Assert sender is null
        assertNull(email.getPlainTextBody()); // Assert body is null
        assertNull(email.getReceivedAt()); // Assert received time is null
        assertNull(email.getIsSpam()); // Assert spam flag is null
        assertNull(email.getTrustScore()); // Assert trust score is null
    }

    @Test // Mark this method as a test
    void testEmailBoundaryValues() { // Test email creation with boundary values
        Email email = Email.builder() // Use builder pattern to create an Email
                .subject("") // Set subject to empty string
                .sender("") // Set sender to empty string
                .plainTextBody("") // Set body to empty string
                .receivedAt(LocalDateTime.now()) // Set received time
                .isSpam(true) // Set spam flag to true
                .trustScore(0) // Set trust score to 0
                .build(); // Build the Email object

        assertEquals("", email.getSubject()); // Assert subject is empty string
        assertEquals("", email.getSender()); // Assert sender is empty string
        assertEquals("", email.getPlainTextBody()); // Assert body is empty string
        assertTrue(email.getIsSpam()); // Assert spam flag is true
        assertEquals(0, email.getTrustScore()); // Assert trust score is 0
        assertNotNull(email.getReceivedAt()); // Assert received time is not null
    }

    @Test // Mark this method as a test
    void testEmailTrustScoreLimits() { // Test trust score limits
        Email email = Email.builder() // Use builder pattern to create an Email
                .trustScore(100) // Set trust score to 100
                .build(); // Build the Email object
        assertEquals(100, email.getTrustScore()); // Assert trust score is 100

        email = Email.builder() // Use builder pattern to create an Email
                .trustScore(0) // Set trust score to 0
                .build(); // Build the Email object
        assertEquals(0, email.getTrustScore()); // Assert trust score is 0

        email = Email.builder() // Use builder pattern to create an Email
                .trustScore(-1) // Set trust score to -1
                .build(); // Build the Email object
        assertEquals(-1, email.getTrustScore()); // Assert trust score is -1

        email = Email.builder() // Use builder pattern to create an Email
                .trustScore(150) // Set trust score to 150
                .build(); // Build the Email object
        assertEquals(150, email.getTrustScore()); // Assert trust score is 150
    }
}
