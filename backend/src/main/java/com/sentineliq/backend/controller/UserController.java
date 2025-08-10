package com.sentineliq.backend.controller;

import com.sentineliq.backend.model.EmailAccount;
import com.sentineliq.backend.model.User;
import com.sentineliq.backend.repository.EmailAccountRepository;
import com.sentineliq.backend.repository.UserRepository;
import com.sentineliq.backend.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UserController handles user-related API endpoints.
 * 
 * - Provides an endpoint to fetch the currently authenticated user's profile and their linked inboxes.
 * - Uses JWT authentication via cookies.
 */
@RestController // Marks this class as a REST controller (returns JSON by default)
@RequestMapping("/api") // Base path for all endpoints in this controller
public class UserController {

    @Autowired
    private JwtUtil jwtUtil; // Utility for JWT operations

    @Autowired
    private UserRepository userRepository; // Repository for User entities

    @Autowired
    private EmailAccountRepository emailAccountRepository; // Repository for EmailAccount entities

    /**
     * GET /api/me
     * Returns the current authenticated user's profile and their linked inboxes.
     * Requires a valid JWT in the 'access_token' cookie.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        String token = null;

        // 🔐 Extract JWT from cookies
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }

        // 🔒 Validate token
        if (token == null || jwtUtil.validateToken(token) == null) {
            return ResponseEntity.status(403).body(Map.of("error", "Authentication required"));
        }

        // 🧠 Extract username from token
        String username = jwtUtil.getUsernameFromToken(token);
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        User user = userOpt.get();

        // 📬 Fetch user's linked email accounts and map to a list of simple objects
        List<Map<String, Object>> inboxes = emailAccountRepository.findAllByUser(user)
            .stream()
            .map(inbox -> {
                Map<String, Object> map = new java.util.HashMap<>();
                map.put("id", inbox.getId());
                map.put("displayName", inbox.getDisplayName());
                map.put("emailAddress", inbox.getEmailAddress());
                map.put("provider", inbox.getProvider());
                map.put("isPrimary", inbox.isPrimary());
                return map;
            })
            .collect(Collectors.toList());

        // ✅ Return user data + inboxes as a JSON object
        return ResponseEntity.ok(Map.of(
            "username", user.getUsername(),
            "name", user.getName(),
            "inboxes", inboxes
        ));
    }
}
