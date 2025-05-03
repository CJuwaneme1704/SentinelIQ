package com.sentineliq.backend.dto;

import lombok.Data;

// ✅ Handles data coming from the signup form on the frontend
@Data
public class SignupRequest {
    private String username;  // 🧑 Desired login username
    private String name;      // 👤 Full name
    private String email;     // 📧 Email address (used later for linking providers)
    private String password;  // 🔐 Raw password (will be encoded)
}
