package com.sentineliq.backend.controller;

import com.sentineliq.backend.service.GmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/gmail")
public class GmailController {

    @Autowired
    private GmailService gmailService;

    @PostMapping("/{accountId}/fetch")
    public ResponseEntity<?> fetchEmails(@PathVariable Long accountId) {
        try {
            gmailService.fetchAndStoreEmails(accountId);
            return ResponseEntity.ok(Map.of("message", "Emails synced"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
