package com.acme.salarymanagement.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record SalaryRecordResponse(
        UUID id,
        UUID employeeId,
        BigDecimal annualSalary,
        String currency,
        LocalDate effectiveFrom,
        Instant createdAt,
        UUID createdBy) {
}