package com.academic.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String SECRET =
            "AcadChainSecretKey2024BlockchainProjectSecure!@#$%^&*";
    private static final long EXPIRATION =
            24 * 60 * 60 * 1000;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(
                SECRET.getBytes());
    }

    public String generateToken(
            Long userId,
            String email,
            String role) {
        Map<String, Object> claims =
                new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(
                        System.currentTimeMillis()
                                + EXPIRATION))
                .signWith(getSigningKey(),
                        SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractClaims(
            String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long extractUserId(
            String token) {
        return Long.valueOf(
                extractClaims(token)
                        .get("userId").toString());
    }

    public String extractRole(
            String token) {
        return (String) extractClaims(token)
                .get("role");
    }

    public String extractEmail(
            String token) {
        return extractClaims(token)
                .getSubject();
    }

    public boolean isTokenValid(
            String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}