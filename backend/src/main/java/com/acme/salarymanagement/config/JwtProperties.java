package com.acme.salarymanagement.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record JwtProperties(String jwtSecret, Duration jwtExpiration) {
}
