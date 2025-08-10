package com.sentineliq.backend.dto;

import lombok.*;  
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailDto {
    private Long id;           // Add this field for unique email ID
    private String subject;
    private String sender;
    private String plainTextBody;
    private String htmlBody;
    private LocalDateTime receivedAt;
    private Boolean isSpam;
    private Integer trustScore;
}
