// src/main/java/com/sentineliq/backend/model/User.java
package com.sentineliq.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// ✅ Marks this class as a JPA entity (table in the database)
@Entity

// ✅ Lombok annotations to reduce boilerplate
@Data // Getters, setters, toString, equals, hashCode
@NoArgsConstructor // No-arg constructor
@AllArgsConstructor // Constructor with all fields

// ✅ Specifies table name
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String role = "USER"; // ✅ Default value if not passed from frontend

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


    // ✅ Automatically set createdAt when inserting
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
