package com.sentineliq.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SignupRequest {
    
    // @NotBlank(message = "Username is required")
    // @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;
    
    // @NotBlank(message = "Email is required")
    // @Email(message = "Invalid email format")
    private String email;
    
    // @NotBlank(message = "Password is required")
    // @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
    
    // @NotBlank(message = "Name is required")
    // @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    public SignupRequest() {}

    public SignupRequest(String username, String email, String password, String name) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.name = name;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
