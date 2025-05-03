package com.sentineliq.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "emails")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_account_id", nullable = false)
    private EmailAccount emailAccount;

    @Column(columnDefinition = "TEXT")
    private String subject;

    @Column(length = 255)
    private String sender;

    @Column(columnDefinition = "TEXT")
    private String body;

    private LocalDateTime receivedAt;

    private Boolean isSpam;

    private Integer trustScore;
}
