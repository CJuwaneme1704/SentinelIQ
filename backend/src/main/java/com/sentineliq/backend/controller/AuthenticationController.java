package com.sentineliq.backend.controller;

import com.sentineliq.backend.dto.LoginRequest;
import com.sentineliq.backend.dto.SignupRequest;
import com.sentineliq.backend.dto.TokenRefreshRequest;
import com.sentineliq.backend.model.User;
import com.sentineliq.backend.repository.UserRepository;
import com.sentineliq.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 📝 Signup Method
   @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> registerUser(@Valid @RequestBody SignupRequest signupRequest, HttpServletResponse response) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("message", "Username is already taken"));
        }
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("message", "Email is already in use"));
        }

        // Create a new user
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setName(signupRequest.getName());
        userRepository.save(user);

        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // Set cookies
        setCookie(response, "access_token", accessToken, 15 * 60); // 15 minutes
        setCookie(response, "refresh_token", refreshToken, 7 * 24 * 60 * 60); // 7 days

        return ResponseEntity.status(HttpStatus.CREATED)
                            .body(Map.of("message", "User registered successfully"));
    }


    // 🔐 Login Method
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        // Find user by username
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body(Map.of("message", "Invalid credentials"));
        }

        User user = userOptional.get();

        // Check password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body(Map.of("message", "Invalid credentials"));
        }

        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        // Set cookies
        setCookie(response, "access_token", accessToken, 15 * 60); // 15 minutes
        setCookie(response, "refresh_token", refreshToken, 7 * 24 * 60 * 60); // 7 days

        return ResponseEntity.ok(Map.of("message", "Login successful"));
    }


    

    // 🔄 Token Refresh Method
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody TokenRefreshRequest refreshRequest, HttpServletResponse response) {
        String refreshToken = refreshRequest.getRefreshToken();

        // Validate the refresh token
        if (jwtUtil.validateToken(refreshToken) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body(Map.of("message", "Invalid refresh token"));
        }

        String username = jwtUtil.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                                  .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate new access token
        String newAccessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole());
        setCookie(response, "access_token", newAccessToken, 15 * 60); // 15 minutes

        return ResponseEntity.ok(Map.of("message", "Token refreshed successfully"));
    }

    // 🚪 Logout Method
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logoutUser(HttpServletResponse response) {
        // Clear cookies
        clearCookie(response, "access_token");
        clearCookie(response, "refresh_token");

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    // Helper method to set cookies
    private void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    private void clearCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, ""); // was null
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

        // ✅ Add this new GET endpoint
    @GetMapping("/check")
    public ResponseEntity<?> checkAuth(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return ResponseEntity.ok().build(); // ✅ Authenticated
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // ❌ Not authenticated
    }





}
