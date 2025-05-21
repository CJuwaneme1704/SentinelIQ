package com.sentineliq.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data // ✅ Adds getters, setters, toString, equals, and hashCode
@NoArgsConstructor // ✅ Adds a no-args constructor
@AllArgsConstructor // ✅ Adds a constructor with all fields
@Builder // ✅ Adds a builder pattern for this class
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Builder.Default
    @Column(nullable = false)
    private String role = "USER";

    @Column(nullable = false, unique = true)
    private String username;


    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmailAccount> emailAccounts = new ArrayList<>();  // ✅ Initialize here

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
