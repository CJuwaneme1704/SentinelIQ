package com.sentineliq.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor // ✅ This is the fix
public class GmailTokenResponse {
    private String email;
    private String accessToken;
    private String refreshToken;
    private int expiresIn;
}