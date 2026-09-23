package com.acme.salarymanagement.dto;

import java.math.BigDecimal;

public record SalaryStatisticsResponse(
        String currency,
        long employeeCount,
        BigDecimal minimumSalary,
        BigDecimal maximumSalary,
        BigDecimal averageSalary) {
}
