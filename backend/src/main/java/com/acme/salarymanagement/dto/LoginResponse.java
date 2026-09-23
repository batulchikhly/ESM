package com.acme.salarymanagement.dto;

import java.time.Instant;

public record LoginResponse(
        String accessToken,
        String tokenType,
        Instant expiresAt,
        String email,
        String role) {
}
