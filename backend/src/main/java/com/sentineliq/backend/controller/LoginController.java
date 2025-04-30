package com.sentineliq.backend.controller;

// Import the User model (represents user data from the database)
import com.sentineliq.backend.model.User;

// Import the UserRepository to query users by email
import com.sentineliq.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired; // For dependency injection
import org.springframework.http.ResponseEntity; // To return HTTP responses with status + body
import org.springframework.web.bind.annotation.*;

import java.util.Optional; // To safely handle results that may or may not be present

// Marks this class as a REST controller that handles HTTP requests
@RestController

// All endpoints in this controller will start with "/api"
@RequestMapping("/api")

// Allow requests from frontend running at http://localhost:3000 (important for CORS)
@CrossOrigin(origins = "http://localhost:3000")

public class LoginController {

    // Automatically inject an instance of UserRepository
    @Autowired
    private UserRepository userRepository;

    // Handle POST requests to "/api/login"
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User requestUser) {
        // @RequestBody maps the JSON sent from the frontend into a User object

        // Look up user by email
        Optional<User> userOptional = userRepository.findByEmail(requestUser.getEmail());

        // If no user is found with the email, return 401 Unauthorized
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(401).body("User not found");
        }

        // If user exists, get the actual User object
        User user = userOptional.get();

        // Check if the entered password matches the one in the database (plaintext for now)
        if (!user.getPassword().equals(requestUser.getPassword())) {
            // If not matching, return 401 Unauthorized
            return ResponseEntity.status(401).body("Invalid password");
        }

        // If both email and password match, return 200 OK with a success message
        return ResponseEntity.ok("Login successful!");
    }
}
