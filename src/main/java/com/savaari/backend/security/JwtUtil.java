package com.savaari.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long EXPIRY = 1000 * 60 * 60 * 24; // 24 hours

    public String generateToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRY))
                .signWith(key)
                .compact();
    }

    public String getPhone(String token) {
        String subject = getClaims(token).getSubject();
        return subject.split("\\|")[0];
    }

    public String getRole(String token) {
        String subject = getClaims(token).getSubject();
        return subject.split("\\|")[1];
    }

    public Long getUserId(String token) {
        String subject = getClaims(token).getSubject();
        String[] parts = subject.split("\\|");
        return parts.length > 2 ? Long.parseLong(parts[2]) : null;
    }

    public boolean isValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}