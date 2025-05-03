package com.sentineliq.backend.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Base64;
import java.util.Map;
import java.util.HashMap;

import com.sentineliq.backend.model.User;

/**
 * Utility class for creating and validating JWT tokens.
 */
@Component
public class JwtUtil {

    // Token expiration time (10 hours)
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10;

    // Inject secret key from application.properties
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Converts base64-encoded secret string to an HMAC SHA-256 key.
     */
    private SecretKey getSigningKey() {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(decodedKey);
    }

    /**
     * ✅ Generates a JWT token for the provided User.
     * Includes user ID as the subject and embeds the username and role as claims.
     *
     * @param user The user to create a token for
     * @return Signed JWT token string
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole());
    
        return Jwts.builder()
            .claims(claims)
            .subject(user.getUsername()) // instead of String.valueOf(user.getId())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(getSigningKey(), Jwts.SIG.HS256)
            .compact();
    }
    
    // /**
    //  * 📥 Extracts user ID (subject) from token.
    //  */
    // public Long extractUserId(String token) {
    //     return Long.parseLong(extractAllClaims(token).getSubject());
    // }

    /**
     * 📥 Extracts username from token claims.
     */
    public String extractUsername(String token) {
        return (String) extractAllClaims(token).get("username");
    }

    /**
     * 📥 Extracts user role from token claims.
     */
    public String extractUserRole(String token) {
        return (String) extractAllClaims(token).get("role");
    }

    /**
     * ✅ Validates the JWT token (only checks expiration for now).
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        return !isTokenExpired(token);
    }

    /**
     * ⛔ Checks if token is expired.
     */
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    /**
     * 🧠 Parses and returns all token claims.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
