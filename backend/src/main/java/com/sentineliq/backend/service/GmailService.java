package com.sentineliq.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.sentineliq.backend.model.Email;
import com.sentineliq.backend.model.EmailAccount;
import com.sentineliq.backend.repository.EmailRepository;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

/**
 * GmailService handles talking to the Gmail API and turning Gmail messages into Email objects for our app.
 * 
 * - Fetches emails from Gmail using OAuth2 tokens.
 * - Converts Gmail messages into our Email model.
 * - Saves emails to the database.
 * - Has helper methods to pull out the text and HTML content from Gmail messages.
 */
@Slf4j
@Service
public class GmailService {

    private final EmailRepository emailRepository;

    // Set up the service with the email repository
    public GmailService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    /**
     * Fetch up to 10 emails from Gmail for the given account and save them.
     * 
     * @param accessToken OAuth2 token for Gmail
     * @param account     The EmailAccount to link emails to
     * @return List of saved Email objects
     */
    public List<Email> fetchAndSaveEmails(String accessToken, EmailAccount account) {
        try {
            // Build a Gmail API client using the access token
            Gmail gmail = buildGmailClient(accessToken);
            log.info("Fetching up to 10 emails for account: {}", account.getEmailAddress());

            // Get up to 10 messages for the user
            ListMessagesResponse messagesResponse = gmail.users().messages()
                    .list("me")
                    .setMaxResults(10L)
                    .execute();

            List<Message> messageRefs = messagesResponse.getMessages();
            if (messageRefs == null || messageRefs.isEmpty()) {
                log.info("No messages found for Gmail account: {}", account.getEmailAddress());
                return Collections.emptyList();
            }

            List<Email> savedEmails = new ArrayList<>();

            // For each message, fetch the full message, convert it, and save it
            for (Message ref : messageRefs) {
                Message fullMessage = gmail.users().messages().get("me", ref.getId()).execute();
                Email email = parseMessageToEmail(fullMessage, account);
                emailRepository.save(email);
                savedEmails.add(email);

                log.info("Saved email: '{}' from {}", email.getSubject(), email.getSender());
            }

            log.info("Finished fetching {} emails for account: {}", savedEmails.size(), account.getEmailAddress());
            return savedEmails;

        } catch (Exception e) {
            log.error("Error while fetching/saving emails for account: {}", account.getEmailAddress(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Get the value of a specific header (like "Subject" or "From") from a list of headers.
     */
    private String extractHeaderValue(List<MessagePartHeader> headers, String headerName) {
        return headers.stream()
                .filter(h -> h.getName().equalsIgnoreCase(headerName))
                .map(MessagePartHeader::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * Get the plain text body from a Gmail message.
     */
    private String extractBody(Message message) {
        return extractBodyFromPart(message.getPayload());
    }

    /**
     * Recursively look for and return the plain text body from a Gmail message part.
     * If not found, returns an empty string.
     */
    private String extractBodyFromPart(MessagePart part) {
        if (part == null) return "";

        // If this part is plain text and has data, decode and return it
        if ("text/plain".equals(part.getMimeType()) && part.getBody() != null && part.getBody().getData() != null) {
            try {
                byte[] decodedBytes = Base64.getUrlDecoder().decode(part.getBody().getData());
                return new String(decodedBytes, StandardCharsets.UTF_8);
            } catch (Exception e) {
                log.warn("Failed to decode email body", e);
                return "";
            }
        }

        // If this part has sub-parts, check each one for plain text
        if (part.getParts() != null) {
            for (MessagePart subPart : part.getParts()) {
                String body = extractBodyFromPart(subPart);
                if (!body.isBlank()) return body;
            }
        }

        // No plain text body found
        return "";
    }

    /**
     * Recursively look for and return the HTML body from a Gmail message part.
     * If not found, returns an empty string.
     */
    private String extractHtmlBodyFromPart(MessagePart part) {
        if (part == null) return "";

        // If this part is HTML and has data, decode and return it
        if ("text/html".equals(part.getMimeType()) && part.getBody() != null && part.getBody().getData() != null) {
            try {
                byte[] decodedBytes = Base64.getUrlDecoder().decode(part.getBody().getData());
                return new String(decodedBytes, StandardCharsets.UTF_8);
            } catch (Exception e) {
                log.warn("Failed to decode HTML body", e);
                return "";
            }
        }

        // If this part has sub-parts, check each one for HTML
        if (part.getParts() != null) {
            for (MessagePart subPart : part.getParts()) {
                String body = extractHtmlBodyFromPart(subPart);
                if (!body.isBlank()) return body;
            }
        }

        // No HTML body found
        return "";
    }

    /**
     * Turn a Gmail Message into our Email object.
     * Pulls out subject, sender, date, plain text, and HTML body.
     */
    private Email parseMessageToEmail(Message message, EmailAccount account) {
        List<MessagePartHeader> headers = message.getPayload().getHeaders();

        String subject = extractHeaderValue(headers, "Subject");
        String sender = extractHeaderValue(headers, "From");
        String dateHeader = extractHeaderValue(headers, "Date");

        // Try to parse the date, use now if it fails
        Instant timestamp = Instant.now();
        if (dateHeader != null) {
            try {
                timestamp = ZonedDateTime.parse(dateHeader, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
            } catch (Exception e) {
                log.warn("Failed to parse date header '{}', using current time instead", dateHeader);
            }
        }

        String plainTextBody = extractBody(message);
        String htmlBody = extractHtmlBodyFromPart(message.getPayload());

        Email email = new Email();
        email.setGmailMessageId(message.getId());
        email.setSubject(subject != null ? subject : "(No Subject)");
        email.setSender(sender != null ? sender : "(Unknown Sender)");
        email.setPlainTextBody(plainTextBody != null ? plainTextBody : "(Empty Body)");
        email.setHtmlBody(htmlBody != null && !htmlBody.isBlank() ? htmlBody : null);
        email.setReceivedAt(LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()));
        email.setEmailAccount(account);
        email.setIsSpam(false); // Default value, can be updated later
        email.setTrustScore(100); // Default value, can be updated later

        return email;
    }

    /**
     * Build and return a Gmail API client using the given access token.
     */
    public Gmail buildGmailClient(String accessToken) throws Exception {
        AccessToken token = new AccessToken(accessToken, null);
        GoogleCredentials credentials = GoogleCredentials.create(token);
        HttpCredentialsAdapter adapter = new HttpCredentialsAdapter(credentials);

        return new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                adapter
        ).setApplicationName("SentinelIQ").build();
    }
}
