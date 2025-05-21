package com.sentineliq.backend.controller; // Package declaration

import com.fasterxml.jackson.databind.ObjectMapper; // Import for JSON serialization/deserialization
import com.sentineliq.backend.dto.LoginRequest; // Import LoginRequest DTO
import com.sentineliq.backend.dto.SignupRequest; // Import SignupRequest DTO
import com.sentineliq.backend.dto.TokenRefreshRequest; // Import TokenRefreshRequest DTO
import com.sentineliq.backend.model.User; // Import User entity
import com.sentineliq.backend.repository.UserRepository; // Import UserRepository
import com.sentineliq.backend.util.JwtUtil; // Import JwtUtil for token operations

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;

import org.junit.jupiter.api.BeforeEach; // Import JUnit BeforeEach annotation
import org.junit.jupiter.api.Test; // Import JUnit Test annotation
import org.mockito.InjectMocks; // Import Mockito InjectMocks annotation
import org.mockito.Mock; // Import Mockito Mock annotation
import org.mockito.MockitoAnnotations; // Import MockitoAnnotations for mock initialization
import org.springframework.http.HttpStatus; // Import HttpStatus
import org.springframework.http.ResponseEntity; // Import ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder; // Import PasswordEncoder
import org.springframework.test.web.servlet.MockMvc; // Import MockMvc for simulating HTTP requests
import org.springframework.test.web.servlet.setup.MockMvcBuilders; // Import MockMvcBuilders

import jakarta.servlet.http.Cookie; // Import Cookie
import jakarta.servlet.http.HttpServletResponse; // Import HttpServletResponse
import java.util.Optional; // Import Optional

import static org.mockito.ArgumentMatchers.any; // Import Mockito any matcher
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*; // Import all Mockito static methods
import static org.assertj.core.api.Assertions.assertThat; // Import AssertJ assertion
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post; // Import MockMvc POST request builder
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status; // Import MockMvc status matcher
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath; // Import MockMvc JSON path matcher
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie; // Import MockMvc cookie matcher

class AuthenticationControllerTest { // Define the test class

    private MockMvc mockMvc; // MockMvc for simulating HTTP requests

    @Mock
    private UserRepository userRepository; // Mocked UserRepository

    @Mock
    private PasswordEncoder passwordEncoder; // Mocked PasswordEncoder

    @Mock
    private JwtUtil jwtUtil; // Mocked JwtUtil

    @InjectMocks
    private AuthenticationController authenticationController; // Inject mocks into AuthenticationController

    private ObjectMapper objectMapper; // ObjectMapper for JSON serialization

    @BeforeEach
    void setUp() {
        // Initialize all mocks before each test
        MockitoAnnotations.openMocks(this);

        // Set up MockMvc for the controller under test
        this.mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();

        // Initialize the ObjectMapper for JSON serialization
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Test the user registration endpoint for a successful registration.
     * This test simulates a POST request to /api/auth/signup and verifies:
     * - The user is saved if username/email are not taken
     * - The password is encoded
     * - Access and refresh tokens are generated and set as cookies
     * - The response status and message are correct
     */
    @Test
    void testRegisterUser_Success() throws Exception {
        // Prepare the signup request with test data
        SignupRequest signupRequest = new SignupRequest("testuser", "test@example.com", "password", "Test User");

        // Mock repository behavior: username and email do not exist
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        // Mock password encoding
        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");

        // Mock JWT generation for access and refresh tokens
        when(jwtUtil.generateAccessToken("testuser", "USER")).thenReturn("access_token");
        when(jwtUtil.generateRefreshToken("testuser")).thenReturn("refresh_token");

        // Perform the POST request to /api/auth/signup with the signupRequest as JSON
        mockMvc.perform(post("/api/auth/signup")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.message").value("User registered successfully")) // Expect success message in JSON
                .andExpect(cookie().value("access_token", "access_token")) // Expect access_token cookie set
                .andExpect(cookie().value("refresh_token", "refresh_token")); // Expect refresh_token cookie set

        // Verify that the user was saved to the repository
        verify(userRepository, times(1)).save(any(User.class));

        // Verify that the tokens were generated
        verify(jwtUtil, times(1)).generateAccessToken("testuser", "USER");
        verify(jwtUtil, times(1)).generateRefreshToken("testuser");
    }

    @Test
    void testRegisterUser_UsernameTaken() throws Exception {
        // Prepare the signup request
        SignupRequest signupRequest = new SignupRequest("testuser", "test@example.com", "password", "Test User");

        // Mock the repository behavior to simulate an existing username
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Perform the request
        mockMvc.perform(post("/api/auth/signup")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username is already taken"));

        // Verify that the user was not saved
        verify(userRepository, never()).save(any(User.class));

        // Verify that the tokens were not generated
        verify(jwtUtil, never()).generateAccessToken(anyString(), anyString());
        verify(jwtUtil, never()).generateRefreshToken(anyString());
    }
    @Test
    void testRegisterUser_EmailTaken() throws Exception {
        // Prepare the signup request
        SignupRequest signupRequest = new SignupRequest("newuser", "test@example.com", "password", "Test User");

        // Mock the repository behavior to simulate an existing email
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Perform the request
        mockMvc.perform(post("/api/auth/signup")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email is already in use"));

        // Verify that the user was not saved
        verify(userRepository, never()).save(any(User.class));

        // Verify that the tokens were not generated
        verify(jwtUtil, never()).generateAccessToken(anyString(), anyString());
        verify(jwtUtil, never()).generateRefreshToken(anyString());
    }

    @Test
    void testRegisterUser_EmptyFields() throws Exception {
        // Prepare the signup request with empty fields
        SignupRequest signupRequest = new SignupRequest("", "", "", "");

        // Perform the request
        mockMvc.perform(post("/api/auth/signup")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest());

        // Verify that the user was not saved
        verify(userRepository, never()).save(any(User.class));

        // Verify that the tokens were not generated
        verify(jwtUtil, never()).generateAccessToken(anyString(), anyString());
        verify(jwtUtil, never()).generateRefreshToken(anyString());
    }
    @Test
    void testRegisterUser_InvalidPassword() throws Exception {
        // Prepare the signup request with a weak password
        SignupRequest signupRequest = new SignupRequest("validuser", "valid@example.com", "123", "Valid User");

        // Perform the request and print the response
        String responseBody = mockMvc.perform(post("/api/auth/signup")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Print the actual response for debugging
        System.out.println("Actual Response: " + responseBody);

        // Verify that the user was not saved
        verify(userRepository, never()).save(any(User.class));

        // Verify that the tokens were not generated
        verify(jwtUtil, never()).generateAccessToken(anyString(), anyString());
        verify(jwtUtil, never()).generateRefreshToken(anyString());
    }

    @Test
    void testLoginUser_Success() throws Exception {
    LoginRequest loginRequest = new LoginRequest("testuser", "validPassword123");
    User user = new User();
    user.setUsername("testuser");
    user.setEmail("test@example.com");
    user.setPassword("hashedPassword");
    user.setName("Test User");

    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("validPassword123", "hashedPassword")).thenReturn(true);
    when(jwtUtil.generateAccessToken("testuser", "USER")).thenReturn("access_token");
    when(jwtUtil.generateRefreshToken("testuser")).thenReturn("refresh_token");

    mockMvc.perform(post("/api/auth/login")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Login successful"))
            .andExpect(cookie().value("access_token", "access_token"))
            .andExpect(cookie().value("refresh_token", "refresh_token"));
    }

    @Test
    void testLoginUser_InvalidCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest("testuser", "wrongpassword");
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("hashedPassword");
        user.setName("Test User");


        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "hashedPassword")).thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }


    @Test
    void testRefreshToken_Success() throws Exception {
        TokenRefreshRequest refreshRequest = new TokenRefreshRequest("valid_refresh_token");
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("hashedPassword");
        user.setName("Test User");

        // Mock the Claims object
        Claims mockClaims = mock(Claims.class);
        when(mockClaims.getSubject()).thenReturn("testuser");

        // Stub the methods
        when(jwtUtil.validateToken("valid_refresh_token")).thenReturn(mockClaims);
        when(jwtUtil.getUsernameFromToken("valid_refresh_token")).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(jwtUtil.generateAccessToken("testuser", "USER")).thenReturn("new_access_token");

        mockMvc.perform(post("/api/auth/refresh")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Token refreshed successfully"))
                .andExpect(cookie().value("access_token", "new_access_token"));
    }




    
   @Test
    void testLogoutUser_Success() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Logged out successfully"))
            .andExpect(cookie().value("access_token", ""))
            .andExpect(cookie().maxAge("access_token", 0))
            .andExpect(cookie().value("refresh_token", ""))
            .andExpect(cookie().maxAge("refresh_token", 0));
    }






}
