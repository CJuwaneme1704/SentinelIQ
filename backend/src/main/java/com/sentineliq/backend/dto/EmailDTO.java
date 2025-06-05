package com.sentineliq.backend.dto;

import com.sentineliq.backend.model.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailDTO {
    private Long id;
    private String subject;
    private String sender;
    private String body;
    private LocalDateTime receivedAt;
    private Boolean isSpam;
    private Integer trustScore;

    public static EmailDTO fromEntity(Email email) {
        return EmailDTO.builder()
                .id(email.getId())
                .subject(email.getSubject())
                .sender(email.getSender())
                .body(email.getBody())
                .receivedAt(email.getReceivedAt())
                .isSpam(email.getIsSpam())
                .trustScore(email.getTrustScore())
                .build();
    }
}
