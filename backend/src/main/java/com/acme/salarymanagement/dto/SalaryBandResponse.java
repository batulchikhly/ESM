package com.acme.salarymanagement.dto;

public record SalaryBandResponse(
        String currency,
        String band,
        long employeeCount) {
}
