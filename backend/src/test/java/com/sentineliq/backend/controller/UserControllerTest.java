package com.sentineliq.backend.controller;

import com.sentineliq.backend.model.User;
import com.sentineliq.backend.repository.UserRepository;
import com.sentineliq.backend.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // 💥 this disables the 401
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void testGetCurrentUser_Success() throws Exception {
        String token = "valid.jwt.token";
        String username = "jerome";

        User mockUser = User.builder()
                .username(username)
                .name("Jerome Uwaneme")
                .build();

        when(jwtUtil.validateToken(token)).thenReturn(mock(io.jsonwebtoken.Claims.class));
        when(jwtUtil.getUsernameFromToken(token)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        mockMvc.perform(get("/api/me").cookie(new Cookie("access_token", token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("jerome"))
                .andExpect(jsonPath("$.name").value("Jerome Uwaneme"));
    }


    @Test
    void testGetCurrentUser_InvalidToken() throws Exception {
        String invalidToken = "invalid.jwt.token"; 
        // Simulate an invalid token sent by the client

        // Mock behavior: JwtUtil returns null when the token is invalid
        when(jwtUtil.validateToken(invalidToken)).thenReturn(null);

        // Perform the GET request with the invalid token in the cookie
        mockMvc.perform(get("/api/me").cookie(new Cookie("access_token", invalidToken)))
                .andExpect(status().isForbidden()) // Expect 403 Forbidden response
                .andExpect(jsonPath("$.error").value("Authentication required")); // Expect error message
    }

    @Test
    void testGetCurrentUser_MissingToken() throws Exception {
        // No cookie provided in the request

        mockMvc.perform(get("/api/me"))
                .andExpect(status().isForbidden()) // Expect 403 Forbidden because no token was sent
                .andExpect(jsonPath("$.error").value("Authentication required")); // Error response expected
    }

    @Test
    void testGetCurrentUser_UserNotFound() throws Exception {
        String token = "valid.jwt.token";
        String username = "jerome";

        // Mock token as valid
        when(jwtUtil.validateToken(token)).thenReturn(mock(io.jsonwebtoken.Claims.class));
        when(jwtUtil.getUsernameFromToken(token)).thenReturn(username);

        // Mock user not found in DB
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/me").cookie(new Cookie("access_token", token)))
                .andExpect(status().isNotFound()) // Expect 404 Not Found if user isn't in DB
                .andExpect(jsonPath("$.error").value("User not found")); // Specific error message
    }

    



}
