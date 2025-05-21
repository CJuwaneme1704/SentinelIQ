package com.sentineliq.backend.util;

import org.junit.jupiter.api.Test; // Import JUnit Test annotation
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest // Enable Spring Boot test context
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil; // Inject JwtUtil bean

    // Old test: manual run and print tokens
    @SpringBootApplication
    public static class JwtUtilTestApp {
        public static void main(String[] args) {
            ApplicationContext context = SpringApplication.run(JwtUtilTestApp.class, args);
            JwtUtil jwtUtil = context.getBean(JwtUtil.class);

            String username = "testuser";
            String role = "USER";

            String accessToken = jwtUtil.generateAccessToken(username, role);
            String refreshToken = jwtUtil.generateRefreshToken(username);

            System.out.println("Access Token:\n" + accessToken);
            System.out.println("\nRefresh Token:\n" + refreshToken);
        }
    }

    // New test: test extracting username from token
    @Test
    void testGetUsernameFromToken() {
        String username = "testuser";
        String token = jwtUtil.generateRefreshToken(username); // Generate a token with the username
        String extractedUsername = jwtUtil.getUsernameFromToken(token); // Extract username from token
        assertEquals(username, extractedUsername); // Assert that the extracted username matches
    }
}
