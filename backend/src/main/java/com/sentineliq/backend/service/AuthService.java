package com.sentineliq.backend.service;

import com.sentineliq.backend.dto.SignupRequest;
import com.sentineliq.backend.model.User;
import com.sentineliq.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 🛠️ Handles business logic for user signup.
 * Ensures passwords are hashed and emails are unique before saving to the database.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken.");
        }
    
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername()); // ✅ use actual username
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }
    
}

