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

@Slf4j
@Service
public class GmailService {

    private final EmailRepository emailRepository;

    public GmailService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    public List<Email> fetchAndSaveEmails(String accessToken, EmailAccount account) {
        try {
            Gmail gmail = buildGmailClient(accessToken);
            log.info("📩 Fetching up to 10 emails for account: {}", account.getEmailAddress());

            ListMessagesResponse messagesResponse = gmail.users().messages()
                    .list("me")
                    .setMaxResults(10L)
                    .execute();

            List<Message> messageRefs = messagesResponse.getMessages();
            if (messageRefs == null || messageRefs.isEmpty()) {
                log.info("📭 No messages found for Gmail account: {}", account.getEmailAddress());
                return Collections.emptyList();
            }

            List<Email> savedEmails = new ArrayList<>();

            for (Message ref : messageRefs) {
                Message fullMessage = gmail.users().messages().get("me", ref.getId()).execute();
                Email email = parseMessageToEmail(fullMessage, account);
                emailRepository.save(email);
                savedEmails.add(email);

                log.info("📥 Saved email: '{}' from {}", email.getSubject(), email.getSender());
            }

            log.info("✅ Finished fetching {} emails for account: {}", savedEmails.size(), account.getEmailAddress());
            return savedEmails;

        } catch (Exception e) {
            log.error("❌ Error while fetching/saving emails for account: {}", account.getEmailAddress(), e);
            return Collections.emptyList();
        }
    }

    private String extractHeaderValue(List<MessagePartHeader> headers, String headerName) {
        return headers.stream()
                .filter(h -> h.getName().equalsIgnoreCase(headerName))
                .map(MessagePartHeader::getValue)
                .findFirst()
                .orElse(null);
    }

    private String extractBody(Message message) {
        return extractBodyFromPart(message.getPayload());
    }

    private String extractBodyFromPart(MessagePart part) {
        if (part == null) return "";

        if ("text/plain".equals(part.getMimeType()) && part.getBody() != null && part.getBody().getData() != null) {
            try {
                byte[] decodedBytes = Base64.getUrlDecoder().decode(part.getBody().getData());
                return new String(decodedBytes, StandardCharsets.UTF_8);
            } catch (Exception e) {
                log.warn("⚠️ Failed to decode email body", e);
                return "";
            }
        }

        if (part.getParts() != null) {
            for (MessagePart subPart : part.getParts()) {
                String body = extractBodyFromPart(subPart);
                if (!body.isBlank()) return body;
            }
        }

        return "";
    }

    private Email parseMessageToEmail(Message message, EmailAccount account) {
        List<MessagePartHeader> headers = message.getPayload().getHeaders();

        String subject = extractHeaderValue(headers, "Subject");
        String sender = extractHeaderValue(headers, "From");
        String dateHeader = extractHeaderValue(headers, "Date");

        Instant timestamp = Instant.now();
        if (dateHeader != null) {
            try {
                timestamp = ZonedDateTime.parse(dateHeader, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
            } catch (Exception e) {
                log.warn("⚠️ Failed to parse date header '{}', using current time instead", dateHeader);
            }
        }

        String body = extractBody(message);

        Email email = new Email();
        email.setSubject(subject != null ? subject : "(No Subject)");
        email.setSender(sender != null ? sender : "(Unknown Sender)");
        email.setBody(body != null ? body : "(Empty Body)");
        email.setReceivedAt(LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault()));
        email.setEmailAccount(account);
        email.setIsSpam(false);         // Default for now
        email.setTrustScore(100);       // Default for now

        return email;
    }

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
