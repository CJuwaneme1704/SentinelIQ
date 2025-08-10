package com.sentineliq.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "email_accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class EmailAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String emailAddress;

    @Column(nullable = false)
    private String displayName;

    private String provider; // e.g., "GMAIL"

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token")
    private String refreshToken;

    private LocalDateTime expiresAt;
    
    private LocalDateTime lastSynced;

    @Column(length = 1000)
    private String notes;

    private boolean isPrimary;

    @OneToMany(mappedBy = "emailAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Email> emails;
}
