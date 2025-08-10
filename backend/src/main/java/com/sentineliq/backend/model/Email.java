package com.sentineliq.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "emails",
    uniqueConstraints = @UniqueConstraint(columnNames = {"gmailMessageId", "email_account_id"})
)
@Builder // ✅ Adds a builder pattern for this class
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
    private String plainTextBody;

    @Column(columnDefinition = "TEXT")
    private String htmlBody;

    @Column(nullable = false)
    private String gmailMessageId;


    private LocalDateTime receivedAt;

    private Boolean isSpam;

    private Integer trustScore;
}
