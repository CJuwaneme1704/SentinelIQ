package com.sentineliq.backend.controller;

import com.sentineliq.backend.repository.EmailAccountRepository;
import com.sentineliq.backend.repository.UserRepository;
import com.sentineliq.backend.service.GmailService;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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


    @Value("${app.frontend.url}")
    private String frontendUrl;

    private final GmailService gmailService;



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
        HttpServletResponse response) {

        if (token == null || token.isBlank()) {
            log.warn("⚠️ Missing access token cookie");
            return ResponseEntity.status(401).body("Missing authentication token");
        }

        log.info("📩 Gmail OAuth callback received with code={}", code);

        RestClient restClient = RestClient.create();

        // 🔄 Step 1: Exchange code for tokens
        log.info("🔁 Exchanging authorization code for tokens...");

        String formBody = "code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)
            + "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
            + "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8)
            + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
            + "&grant_type=authorization_code";

        Map<String, Object> tokenResponse;
        try {
            tokenResponse = restClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(formBody)
                .retrieve()
                .body(Map.class);
            log.info("✅ Token exchange successful");
        } catch (Exception e) {
            log.error("❌ Failed to exchange code for tokens", e);
            return ResponseEntity.status(502).body(Map.of("error", "Token exchange with Google failed"));
        }

        if (tokenResponse == null) {
            log.error("❌ Token response from Google was null");
            return ResponseEntity.status(502).body(Map.of("error", "No token response from Google"));
        }

        String accessToken = (String) tokenResponse.get("access_token");
        String refreshToken = (String) tokenResponse.get("refresh_token");
        int expiresIn = tokenResponse.get("expires_in") instanceof Integer
            ? (int) tokenResponse.get("expires_in")
            : Integer.parseInt(tokenResponse.get("expires_in").toString());

        log.info("🔐 Access token received, expires in {} seconds", expiresIn);

        // 🔍 Step 2: Get user email from Google
        Map<String, Object> userInfo;
        try {
            userInfo = restClient.get()
                .uri("https://www.googleapis.com/oauth2/v2/userinfo")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(Map.class);
            log.info("✅ Fetched user info from Google");
        } catch (Exception e) {
            log.error("❌ Failed to fetch user info from Google", e);
            return ResponseEntity.status(502).body(Map.of("error", "Failed to fetch Gmail profile"));
        }

        if (userInfo == null || userInfo.get("email") == null) {
            log.error("❌ Missing email in user info response");
            return ResponseEntity.status(502).body(Map.of("error", "Email not found in Google response"));
        }

        String email = (String) userInfo.get("email");
        log.info("📧 Gmail address: {}", email);

        // 🧠 Step 3: Get current user from JWT
        String username;
        try {
            username = jwtUtil.getUsernameFromToken(token);
            log.info("🔐 Authenticated username from JWT: {}", username);
        } catch (Exception e) {
            log.error("❌ Failed to decode JWT", e);
            return ResponseEntity.status(401).body("Invalid access token");
        }

        Optional<User> userOpt = userRepo.findByUsername(username);
        if (userOpt.isEmpty()) {
            log.error("❌ No user found for username: {}", username);
            return ResponseEntity.status(401).body("User not found");
        }

        User user = userOpt.get();

        // Step 4: Check if this Gmail is already linked
        if (emailAccountRepo.existsByEmailAddress(email)) {
            log.warn("⚠️ Gmail address {} already linked", email);
            return ResponseEntity.status(409).body("This Gmail account is already linked.");
        }

        // Step 5: Save new EmailAccount
        EmailAccount newAccount = EmailAccount.builder()
            .user(user)
            .displayName(email)
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
        log.info("✅ Gmail account saved for user {}", user.getUsername());

        // Step 6: Fetch and save emails
        try {
            gmailService.fetchAndSaveEmails(accessToken, newAccount);
            log.info("📥 Emails fetched and saved for {}", email);
        } catch (Exception e) {
            log.error("❌ Failed to fetch emails from Gmail API", e);
            return ResponseEntity.status(500).body("Failed to fetch Gmail messages");
        }

        // Step 7: Redirect to dashboard
        try {
            log.info("🚀 Redirecting to dashboard for inbox {}", newAccount.getId());
            response.sendRedirect(frontendUrl + "/user_pages/protected/dashboard?inboxId=" + newAccount.getId());

        } catch (IOException e) {
            log.error("❌ Redirect failed", e);
            return ResponseEntity.status(500).body("Failed to redirect");
        }

        return null;
    }





    




}
