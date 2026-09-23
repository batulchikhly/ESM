package com.acme.salarymanagement.dto;

import java.math.BigDecimal;

public record DepartmentAnalyticsResponse(
        String department,
        String currency,
        long employeeCount,
        BigDecimal minimumSalary,
        BigDecimal maximumSalary,
        BigDecimal averageSalary) {
}
