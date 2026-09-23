package com.acme.salarymanagement.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SalarySummary(BigDecimal annualSalary, String currency, LocalDate effectiveFrom) {
}
