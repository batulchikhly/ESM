package com.acme.salarymanagement.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final String ISSUER = "acme-salary-management";
    private static final String DEVELOPMENT_SECRET =
            "acme-local-jwt-secret-change-before-production-2026-32-bytes-minimum-key";
    private final JwtProperties properties;
    private final SecretKey signingKey;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        String configuredSecret = properties.jwtSecret();
        if (configuredSecret == null || configuredSecret.getBytes(StandardCharsets.UTF_8).length < 32) {
            configuredSecret = DEVELOPMENT_SECRET;
        }
        this.signingKey = Keys.hmacShaKeyFor(configuredSecret.getBytes(StandardCharsets.UTF_8));
    }

    public IssuedToken issue(String email, String role) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(properties.jwtExpiration());
        String token = Jwts.builder()
                .claims(Map.of("role", role))
                .issuer(ISSUER)
                .subject(email)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey)
                .compact();
        return new IssuedToken(token, expiresAt);
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(ISSUER)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public record IssuedToken(String value, Instant expiresAt) {
    }
}