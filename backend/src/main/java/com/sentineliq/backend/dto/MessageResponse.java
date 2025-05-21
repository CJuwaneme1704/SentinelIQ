package com.sentineliq.backend.dto;

// DTO for sending standardized messages to the client
public class MessageResponse {
    
    private String message;

    // Constructors
    public MessageResponse() {
    }

    public MessageResponse(String message) {
        this.message = message;
    }

    // Getter and Setter
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
