package com.sentineliq.backend.controller; 
// This sets the package location. 'controller' package holds all your API endpoint classes.

import com.sentineliq.backend.model.User; 
// Import your User model (represents users in the database)

import com.sentineliq.backend.repository.UserRepository; 
// Import your UserRepository (to interact with the database)

import org.springframework.beans.factory.annotation.Autowired; 
// Import @Autowired (for dependency injection — Spring will automatically give you an instance of UserRepository)

import org.springframework.http.ResponseEntity; 
// Import ResponseEntity (helps you return HTTP responses with a status code + body)

import org.springframework.web.bind.annotation.*; 
// Import Spring MVC annotations (@RestController, @RequestMapping, etc.)

// Tell Spring Boot that this class handles HTTP requests (API endpoints)
@RestController

// Base path for all endpoints in this controller will start with "/api"
@RequestMapping("/api")

// Allow cross-origin requests (like from your frontend running on localhost:3000)
// Otherwise browser security (CORS) would block your React/Next.js frontend from talking to backend
@CrossOrigin(origins = "http://localhost:3000")

public class SignUpController {

    // Inject (automatically give) an instance of UserRepository to this class
    @Autowired
    private UserRepository userRepository;

    // Handle HTTP POST requests sent to "/api/signup"
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) {
        // @RequestBody means: take the JSON sent in the POST request and map it into a User object

        // Check if the user's email already exists in the database
        if (userRepository.existsByEmail(user.getEmail())) {
            // If yes, return a 400 Bad Request response saying "Email already in use!"
            return ResponseEntity.badRequest().body("Email already in use!");
        }

        // Otherwise, save the new user object into the database
        userRepository.save(user);

        // Return a 200 OK response saying "User registered successfully!"
        return ResponseEntity.ok("User registered successfully!");
    }
}
