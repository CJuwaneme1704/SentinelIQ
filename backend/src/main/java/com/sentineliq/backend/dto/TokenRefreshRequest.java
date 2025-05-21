package com.sentineliq.backend.dto;

// DTO for capturing refresh token requests from the client
public class TokenRefreshRequest {
    private String refreshToken;

    // Constructors
    public TokenRefreshRequest() {
    }

    public TokenRefreshRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // Getters and Setters
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
