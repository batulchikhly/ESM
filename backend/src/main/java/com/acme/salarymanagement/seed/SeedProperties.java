package com.acme.salarymanagement.seed;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.seed")
public record SeedProperties(boolean enabled, long randomSeed, String version, int employeeCount) {
}
