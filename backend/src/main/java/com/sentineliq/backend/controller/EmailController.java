package com.sentineliq.backend.controller;

import com.sentineliq.backend.dto.EmailDto;
import com.sentineliq.backend.model.Email;
import com.sentineliq.backend.model.EmailAccount;
import com.sentineliq.backend.repository.EmailAccountRepository;
import com.sentineliq.backend.repository.EmailRepository;
import com.sentineliq.backend.service.GmailService;
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

    // This controller handles all email-related API endpoints for Gmail inboxes.
    // It lets authenticated users:
    // - Get all emails for one of their inboxes
    // - Get a single email by its ID
    // - Resync (refresh) emails from Gmail for a specific inbox
    // All endpoints require a valid JWT in the 'access_token' cookie and only allow access to the user's own inboxes/emails.

    // Inject repositories and utilities via constructor
    private final EmailAccountRepository emailAccountRepo;
    private final EmailRepository emailRepo;
    private final JwtUtil jwtUtil;
    private final GmailService gmailService;


    /**
     * Get all emails for a specific inbox.
     * Only allows access if the inbox belongs to the authenticated user.
     * Requires a valid JWT in the 'access_token' cookie.
     *
     * @param inboxId The ID of the inbox to fetch emails for
     * @param token   JWT from cookie for authentication
     * @return        200 OK with emails, 401/403/404 otherwise
     */
    @GetMapping("/emails")
    public ResponseEntity<?> getEmailsByInbox(
            @RequestParam("inboxId") Long inboxId,
            @CookieValue(value = "access_token", required = false) String token
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
                        .plainTextBody(email.getPlainTextBody())
                        .htmlBody(email.getHtmlBody())
                        .receivedAt(email.getReceivedAt())
                        .isSpam(email.getIsSpam())
                        .trustScore(email.getTrustScore())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(emailDTOs); // Return the list of emails
    }

    /**
     * Get a single email by its ID.
     * Only allows access if the email belongs to the authenticated user.
     * Requires a valid JWT in the 'access_token' cookie.
     *
     * @param id    The ID of the email to fetch
     * @param token JWT from cookie for authentication
     * @return      200 OK with email, 401/403/404 otherwise
     */
    @GetMapping("/emails/{id}")
    public ResponseEntity<?> getEmailById(
            @PathVariable Long id,
            @CookieValue(value = "access_token", required = false) String token
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
                .plainTextBody(email.getPlainTextBody())
                .htmlBody(email.getHtmlBody())
                .receivedAt(email.getReceivedAt())
                .isSpam(email.getIsSpam())
                .trustScore(email.getTrustScore())
                .build();

        return ResponseEntity.ok(dto); // Return the email DTO
    }

    /**
     * Resync (refresh) emails for an inbox from Gmail.
     * Only allows resync if the inbox belongs to the authenticated user.
     * Requires a valid JWT in the 'access_token' cookie.
     *
     * @param inboxId The ID of the inbox to resync
     * @param token   JWT from cookie for authentication
     * @return        200 OK if resync successful, 401/403/404/500 otherwise
     */
    @PostMapping("/resync")
    public ResponseEntity<?> resyncInbox(
            @RequestParam("inboxId") Long inboxId,
            @CookieValue(value = "access_token", required = false) String token) {

        if (token == null || token.isBlank()) {
            return ResponseEntity.status(401).body("Missing authentication token");
        }

        String username;
        try {
            username = jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token");
        }

        Optional<EmailAccount> inboxOpt = emailAccountRepo.findById(inboxId);
        if (inboxOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Inbox not found");
        }

        EmailAccount inbox = inboxOpt.get();

        if (!inbox.getUser().getUsername().equals(username)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        try {
            gmailService.fetchAndSaveEmails(inbox.getAccessToken(), inbox);
            return ResponseEntity.ok("Resync successful");
        } catch (Exception e) {
            log.error("❌ Resync failed for inbox {}", inboxId, e);
            return ResponseEntity.status(500).body("Failed to resync inbox");
        }
    }

    /**
     * Delete an inbox by its ID.
     * Only allows deletion if the inbox belongs to the authenticated user.
     * Requires a valid JWT in the 'access_token' cookie.
     *
     * @param inboxId The ID of the inbox to delete
     * @param token   JWT from cookie for authentication
     * @return        200 OK if deleted, 401/403/404/500 otherwise
     */
    @DeleteMapping("/inboxes/{inboxId}")
    public ResponseEntity<?> deleteInbox(
            @PathVariable Long inboxId,
            @CookieValue(value = "access_token", required = false) String token) {

        // Check for missing authentication token
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

        // Find the inbox by ID
        Optional<EmailAccount> inboxOpt = emailAccountRepo.findById(inboxId);
        if (inboxOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Inbox not found");
        }

        EmailAccount inbox = inboxOpt.get();

        // Check if the inbox belongs to the authenticated user
        if (!inbox.getUser().getUsername().equals(username)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        try {
            // Delete the inbox (emails will also be deleted if cascade is set)
            emailAccountRepo.delete(inbox);
            log.info("🗑️ Inbox ID {} deleted by user {}", inboxId, username);
            return ResponseEntity.ok("Inbox deleted successfully");
        } catch (Exception e) {
            log.error("❌ Failed to delete inbox ID {}", inboxId, e);
            return ResponseEntity.status(500).body("Failed to delete inbox");
        }
    }

}
