package com.acme.salarymanagement.dto;

import java.math.BigDecimal;

public record CountryAnalyticsResponse(
        String country,
        String currency,
        long employeeCount,
        BigDecimal minimumSalary,
        BigDecimal maximumSalary,
        BigDecimal averageSalary) {
}
