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
public class EmailAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)  // ✅ Ensure this relationship is mandatory
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)  // ✅ Make email address unique to prevent duplicates
    private String emailAddress;

    private String provider;

    @Column(name = "access_token")
    private String accessToken;
    
    @Column(name = "refresh_token")
    private String refreshToken;

    private LocalDateTime lastSynced;

    @OneToMany(mappedBy = "emailAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Email> emails;
}
