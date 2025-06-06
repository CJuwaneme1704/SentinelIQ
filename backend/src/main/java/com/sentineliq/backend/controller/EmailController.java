package com.sentineliq.backend.controller;

import com.sentineliq.backend.dto.EmailDto;
import com.sentineliq.backend.model.Email;
import com.sentineliq.backend.model.EmailAccount;
import com.sentineliq.backend.repository.EmailAccountRepository;
import com.sentineliq.backend.repository.EmailRepository;
import com.sentineliq.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j // Enables logging for this class
@RestController // Marks this class as a REST controller
@RequestMapping("/api/gmail") // Base path for all endpoints in this controller
@RequiredArgsConstructor // Lombok annotation to generate constructor for final fields
public class EmailController {

    // Inject repositories and utilities via constructor
    private final EmailAccountRepository emailAccountRepo;
    private final EmailRepository emailRepo;
    private final JwtUtil jwtUtil;

    /**
     * Endpoint to get emails for a specific inbox.
     * Requires a valid JWT in the 'access_token' cookie.
     * Only allows access if the inbox belongs to the authenticated user.
     */
    @GetMapping("/emails")
    public ResponseEntity<?> getEmailsByInbox(
            @RequestParam("inboxId") Long inboxId, // Inbox ID to fetch emails for
            @CookieValue(value = "access_token", required = false) String token // JWT from cookie
    ) {
        // Check for missing JWT
        if (token == null || token.isBlank()) {
            log.warn("⚠️ Missing JWT in request cookie");
            return ResponseEntity.status(401).body("Missing authentication token");
        }

        String username;
        try {
            // Extract username from JWT
            username = jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            log.error("❌ Failed to decode JWT", e);
            return ResponseEntity.status(401).body("Invalid authentication token");
        }

        // Find the inbox by ID
        Optional<EmailAccount> inboxOpt = emailAccountRepo.findById(inboxId);
        if (inboxOpt.isEmpty()) {
            log.warn("❌ Inbox ID {} not found", inboxId);
            return ResponseEntity.status(404).body("Inbox not found");
        }

        EmailAccount inbox = inboxOpt.get();

        // Check if the inbox belongs to the authenticated user
        if (!inbox.getUser().getUsername().equals(username)) {
            log.warn("🚫 User {} tried to access inbox ID {} they do not own", username, inboxId);
            return ResponseEntity.status(403).body("Access denied");
        }

        // Fetch emails for the inbox, ordered by received date (descending)
        List<Email> emails = emailRepo.findByEmailAccountIdOrderByReceivedAtDesc(inbox.getId());
        log.info("📬 Found {} emails for inbox ID {}", emails.size(), inboxId);

        // Convert Email entities to DTOs for the response
        List<EmailDto> emailDTOs = emails.stream()
                .map(email -> EmailDto.builder()
                        .id(email.getId())
                        .subject(email.getSubject())
                        .sender(email.getSender())
                        .body(email.getBody())
                        .receivedAt(email.getReceivedAt())
                        .isSpam(email.getIsSpam())
                        .trustScore(email.getTrustScore())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(emailDTOs); // Return the list of emails
    }

    /**
     * Endpoint to get a single email by its ID.
     * Requires a valid JWT in the 'access_token' cookie.
     * Only allows access if the email belongs to the authenticated user.
     */
    @GetMapping("/emails/{id}")
    public ResponseEntity<?> getEmailById(
            @PathVariable Long id, // Email ID to fetch
            @CookieValue(value = "access_token", required = false) String token // JWT from cookie
    ) {
        // Check for missing JWT
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(401).body("Missing authentication token");
        }

        String username;
        try {
            // Extract username from JWT
            username = jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token");
        }

        // Find the email by ID
        Optional<Email> emailOpt = emailRepo.findById(id);
        if (emailOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Email not found");
        }

        Email email = emailOpt.get();
        // Check if the email belongs to the authenticated user
        if (!email.getEmailAccount().getUser().getUsername().equals(username)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        // Convert Email entity to DTO for the response
        EmailDto dto = EmailDto.builder()
                .id(email.getId())
                .subject(email.getSubject())
                .sender(email.getSender())
                .body(email.getBody())
                .receivedAt(email.getReceivedAt())
                .isSpam(email.getIsSpam())
                .trustScore(email.getTrustScore())
                .build();

        return ResponseEntity.ok(dto); // Return the email DTO
    }
}
