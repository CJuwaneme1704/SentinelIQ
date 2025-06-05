package com.sentineliq.backend.controller;

import com.sentineliq.backend.dto.GmailTokenResponse;
import com.sentineliq.backend.repository.EmailAccountRepository;
import com.sentineliq.backend.repository.UserRepository;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.Optional;
import java.net.URLEncoder;
import com.sentineliq.backend.model.EmailAccount;
import com.sentineliq.backend.model.User;
import com.sentineliq.backend.util.JwtUtil;




@Slf4j
@RestController
@RequestMapping("/auth/gmail")
@RequiredArgsConstructor
public class GmailOAuthController {

    private final EmailAccountRepository emailAccountRepo;
    private final UserRepository userRepo;

    private final JwtUtil jwtUtil;


    @Value("${gmail.client.id}")
    private String clientId;

    @Value("${gmail.client.secret}")
    private String clientSecret;

    @Value("${gmail.redirect.uri}")
    private String redirectUri;

    @Value("${gmail.oauth.scopes}")
    private String scopes;


    // We'll add methods here step by step

    @GetMapping
    public void startGmailOAuth(HttpServletResponse response) throws IOException {
    // 🔧 Build the Google OAuth URL with required parameters
        String authUrl = UriComponentsBuilder.fromUriString("https://accounts.google.com/o/oauth2/v2/auth")
                .queryParam("client_id", clientId) // Your app's client ID
                .queryParam("redirect_uri", redirectUri) // Where Google should redirect after consent
                .queryParam("response_type", "code") // We want an authorization code
                .queryParam("scope", scopes)
                // 📬 Scopes: Gmail read-only + basic email identity
                .queryParam("access_type", "offline") // Needed to get a refresh token for long-term access
                .queryParam("prompt", "consent") // Always show the consent screen to get refresh token
                .build()
                .toUriString();

        // 🔁 Redirect the user to Google's OAuth screen
        response.sendRedirect(authUrl);
    }
    
    @SuppressWarnings("unchecked")
    @GetMapping("/callback")
    public ResponseEntity<?> handleGmailCallback(
        @RequestParam("code") String code,
        @CookieValue(value = "access_token", required = false) String token,
        HttpServletResponse response)
        {
        
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(401).body("Missing authentication token");
        }



        // 🔧 Step 1: Initialize the new Spring 3.2+ HTTP client
        RestClient restClient = RestClient.create();

         // 🧠 Logging the callback trigger
        log.info("📩 Received Gmail OAuth callback with code={}", code);


        // // 📦 Step 2: Prepare the form data to exchange authorization code for tokens
        // Map<String, String> tokenRequest = Map.of(
        //     "code", code,                             // The code from Google's redirect
        //     "client_id", clientId,                    // Your Google app's client ID
        //     "client_secret", clientSecret,            // Your Google app's client secret
        //     "redirect_uri", redirectUri,              // Must match the one used during redirect
        //     "grant_type", "authorization_code"        // Standard for exchanging code
        // );

        // // 🚀 Step 3: Send POST request to Google's token endpoint to receive access & refresh tokens
        // Map<String, Object> tokenResponse = restClient.post()
        //     .uri("https://oauth2.googleapis.com/token")                   // Token exchange URL
        //     .contentType(MediaType.APPLICATION_FORM_URLENCODED)          // Required by Google's API
        //     .body(tokenRequest)                                          // The body with code/client info
        //     .retrieve()                                                  // Execute request
        //     .body(Map.class);                                            // Convert response to Map

        String formBody = "code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)
            + "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
            + "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8)
            + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
            + "&grant_type=authorization_code";

        Map<String, Object> tokenResponse = restClient.post()
            .uri("https://oauth2.googleapis.com/token")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .body(formBody)
            .retrieve()
            .body(Map.class);







        // ✅ Step 4: Extract token values from response, with null check
        if (tokenResponse == null) {
            return ResponseEntity.status(502).body(Map.of("error", "Failed to retrieve token from Google"));
        }
        String accessToken = (String) tokenResponse.get("access_token");     // Short-term token
        String refreshToken = (String) tokenResponse.get("refresh_token");   // Long-term token
        int expiresIn = tokenResponse.get("expires_in") instanceof Integer
                ? (int) tokenResponse.get("expires_in")
                : Integer.parseInt(tokenResponse.get("expires_in").toString()); // Handle possible type

        // 📥 Step 5: Use the access token to get the user's Gmail account info (like email address)
        Map<String, Object> userInfo = restClient.get()
            .uri("https://www.googleapis.com/oauth2/v2/userinfo")        // Gmail userinfo endpoint
            .header("Authorization", "Bearer " + accessToken)            // Use the token as a Bearer token
            .retrieve()
            .body(Map.class);

        // 🔍 Step 6: Extract the user's Gmail address from the response, with null check
        if (userInfo == null || userInfo.get("email") == null) {
            return ResponseEntity.status(502).body(Map.of("error", "Failed to retrieve user info from Google"));
        }
        String email = (String) userInfo.get("email");
        log.info("📧 Gmail account linked: {}", email);


        // 🧠 Extract username from JWT
        String username = jwtUtil.getUsernameFromToken(token);
        log.info("🔐 Authenticated user: {}", username);


        // ✅ DB LOGIC STARTS HERE
        Optional<User> userOpt = userRepo.findByUsername(username); // 🔒 Replace this later with real JWT-based lookup
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("User not found or not authenticated");
        }
        User user = userOpt.get();

        if (emailAccountRepo.existsByEmailAddress(email)) {
            return ResponseEntity.status(409).body("This Gmail account is already linked.");
        }

        // 📦 Save to DB
        EmailAccount newAccount = EmailAccount.builder()
            .user(user)
            .displayName(email) // Later you can accept from frontend
            .emailAddress(email)
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiresAt(LocalDateTime.now().plusSeconds(expiresIn))
            .provider("GMAIL")
            .notes("Linked via OAuth on " + LocalDateTime.now())
            .lastSynced(null)
            .isPrimary(false)
            .build();

        emailAccountRepo.save(newAccount);
        log.info("✅ Saved Gmail account {} to DB for user {}", email, user.getUsername());



        // 🧾 Step 7: Return all key details (for now — we'll save to DB next)
        try {
           response.sendRedirect("http://localhost:3000/user_pages/protected/dashboard?refresh=true");
        } catch (IOException e) {
            log.error("Failed to redirect to dashboard", e);
            return ResponseEntity.status(500).body("Failed to redirect to dashboard");
        }
        return null;



    }



}
