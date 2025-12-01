package com.dangerzone.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.SignatureException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtUtil {

    private final Key secretKey;
    private final long expirationMs;

    public JwtUtil(@Value("${JWT_SECRET:FallbackSecretKey123!}") String secret,
                   @Value("${JWT_EXPIRATION_MS:36000000}") long expirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    // Gera token com o ID
    public String generateToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    // Extrai o ID do usuário do token
    public Long extractUserId(String token) {
        return Long.valueOf(getClaims(token).getSubject());
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
            return true;

        } catch (ExpiredJwtException e) {
            System.out.println("TOKEN ERROR → Token expirado: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("TOKEN ERROR → Token não suportado: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("TOKEN ERROR → Token mal formado: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("TOKEN ERROR → Token vazio ou nulo: " + e.getMessage());
        }

        return false;
    }
    
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
