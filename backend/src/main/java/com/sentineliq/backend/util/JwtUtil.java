package com.sentineliq.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.nio.charset.StandardCharsets;
//import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.List;



@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;



    private static final long ACCESS_TOKEN_EXPIRY = 15*60*1000;
    

    private static final long REFRESH_TOKEN_EXPIRY = 7 * 24 * 60 * 60 * 1000; // 7 days

    
     // Generate the signing key using the secret from application.properties
    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    
    // Generate an access token (15 minutes expiry)
    public String generateAccessToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", role);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY))
                .signWith(getSigningKey(),io.jsonwebtoken.Jwts.SIG.HS256)
                .compact();
    }

    // Generate a refresh token (7 days expiry)
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY))
                .signWith(getSigningKey(), io.jsonwebtoken.Jwts.SIG.HS256)
                .compact();
    }


    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

   
     // Rotate the refresh token (invalidate the old one)
    public String rotateRefreshToken(String oldToken) {
        try {
            Claims claims = validateToken(oldToken);
            String username = claims.get("username", String.class);
            return generateRefreshToken(username);
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token");
        }
    }

    // Extract the username from a JWT token
    public String getUsernameFromToken(String token) {
        Claims claims = validateToken(token); // Validate and parse the token
        return claims.get("username", String.class); // Return the username claim
    }
    

    public String getRoleFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

    // Helper method to extract all claims from a token
    private Claims extractAllClaims(String token) {
        return validateToken(token);
    }

    public List<SimpleGrantedAuthority> getAuthorities(String role) {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
    }

    

    



}
