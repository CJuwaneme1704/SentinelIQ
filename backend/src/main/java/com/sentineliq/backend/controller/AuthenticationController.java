package com.sentineliq.backend.controller;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.sentineliq.backend.config.JwtUtil;
import com.sentineliq.backend.dto.LoginRequest;
import com.sentineliq.backend.dto.SignupRequest;
import com.sentineliq.backend.model.User;
import com.sentineliq.backend.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;

import com.sentineliq.backend.repository.UserRepository;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:3000")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AuthService authService;

    /**
     * Handles user login using username and password.
     * If successful, returns a JWT token in the response.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            String token = jwtUtil.generateToken(user);

            // Set token as HttpOnly cookie
            ResponseCookie jwtCookie = ResponseCookie.from("jwt", token)
                    .httpOnly(true)
                    .secure(false) // true in prod (HTTPS)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60) // 7 days
                    .sameSite("Lax")
                    .build();

            response.setHeader("Set-Cookie", jwtCookie.toString());
            return ResponseEntity.ok("Login successful");

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    

    /**
     * Handles user signup.
     * If registration is successful, returns a success message.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            authService.registerUser(request);
            return ResponseEntity.ok("Signup successful");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // /**
    //  * Optional protected endpoint for testing token validation.
    //  */
    // @GetMapping("/me")
    // public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
    //     String token = authHeader.replace("Bearer ", "");
    //     Long userId = jwtUtil.extractUserId(token);
    //     return ResponseEntity.ok(userRepository.findById(userId));
    // }
}
