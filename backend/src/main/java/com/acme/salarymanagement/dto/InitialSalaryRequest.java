package com.acme.salarymanagement.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;

public record InitialSalaryRequest(
        @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal annualSalary,
        @NotBlank @Pattern(regexp = "[A-Z]{3}", message = "Must be an ISO 4217 currency code") String currency,
        @NotNull LocalDate effectiveFrom) {
}
