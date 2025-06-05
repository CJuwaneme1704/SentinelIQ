package com.sentineliq.backend.controller;

import com.sentineliq.backend.dto.EmailDTO;
import com.sentineliq.backend.model.Email;
import com.sentineliq.backend.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class EmailController {

    @Autowired
    private EmailRepository emailRepository;

    @GetMapping("/emailAccounts/{accountId}/emails")
    public ResponseEntity<List<EmailDTO>> getEmailsByAccount(@PathVariable Long accountId) {
        List<Email> emails = emailRepository.findByEmailAccountIdOrderByReceivedAtDesc(accountId);
        List<EmailDTO> dtos = emails.stream()
                .map(EmailDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
