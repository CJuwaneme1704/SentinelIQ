package com.sentineliq.backend.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.sentineliq.backend.model.Email;
import com.sentineliq.backend.model.EmailAccount;
import com.sentineliq.backend.repository.EmailAccountRepository;
import com.sentineliq.backend.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class GmailService {

    @Value("${gmail.client.id}")
    private String clientId;

    @Value("${gmail.client.secret}")
    private String clientSecret;

    @Autowired
    private EmailAccountRepository emailAccountRepository;

    @Autowired
    private EmailRepository emailRepository;

    public void fetchAndStoreEmails(Long accountId) throws IOException, GeneralSecurityException {
        EmailAccount account = emailAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Email account not found"));

        GoogleCredential credential = new GoogleCredential.Builder()
                .setClientSecrets(clientId, clientSecret)
                .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                .setJsonFactory(GsonFactory.getDefaultInstance())
                .build()
                .setAccessToken(account.getAccessToken())
                .setRefreshToken(account.getRefreshToken());

        Gmail service = new Gmail.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(), credential)
                .setApplicationName("SentinelIQ")
                .build();

        ListMessagesResponse response = service.users().messages().list("me")
                .setMaxResults(10L)
                .execute();

        List<Email> emails = new ArrayList<>();
        if (response.getMessages() != null) {
            for (Message m : response.getMessages()) {
                Message full = service.users().messages().get("me", m.getId()).execute();
                String subject = getHeader(full, "Subject");
                String from = getHeader(full, "From");
                LocalDateTime received = Instant.ofEpochMilli(full.getInternalDate())
                        .atZone(ZoneId.systemDefault()).toLocalDateTime();
                Email email = Email.builder()
                        .emailAccount(account)
                        .subject(subject)
                        .sender(from)
                        .body(full.getSnippet())
                        .receivedAt(received)
                        .isSpam(false)
                        .trustScore(0)
                        .build();
                emails.add(email);
            }
        }
        emailRepository.saveAll(emails);
        account.setLastSynced(LocalDateTime.now());
        emailAccountRepository.save(account);
    }

    private String getHeader(Message message, String name) {
        if (message.getPayload() == null || message.getPayload().getHeaders() == null) {
            return "";
        }
        for (MessagePartHeader h : message.getPayload().getHeaders()) {
            if (name.equalsIgnoreCase(h.getName())) {
                return h.getValue();
            }
        }
        return "";
    }
}
