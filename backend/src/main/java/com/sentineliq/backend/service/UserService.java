package com.sentineliq.backend.service;

import com.sentineliq.backend.model.User;
import com.sentineliq.backend.repository.UserRepository;
import com.sentineliq.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Service;
import java.util.Optional;

public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    // Handle user login
    public String login(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return jwtUtil.generateAccessToken(username, user.get().getRole());
        }

        throw new RuntimeException("Invalid username or password");

    }

    // Handle user registration
    public String register(String username, String email, String password, String name) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setName(name);
        user.setRole("USER");

        userRepository.save(user);

        return jwtUtil.generateAccessToken(username, "USER");
    }

    // Handle token rotation on logout
    public String logout(String oldRefreshToken) {
        return jwtUtil.rotateRefreshToken(oldRefreshToken);
    }







}
