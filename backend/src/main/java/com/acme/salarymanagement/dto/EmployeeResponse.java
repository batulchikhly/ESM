package com.acme.salarymanagement.dto;

import com.acme.salarymanagement.model.EmploymentStatus;
import java.time.Instant;
import java.util.UUID;

public record EmployeeResponse(
        UUID id,
        String employeeCode,
        String firstName,
        String lastName,
        String email,
        String country,
        String department,
        String jobTitle,
        EmploymentStatus employmentStatus,
        Instant createdAt,
        Instant updatedAt,
        SalarySummary currentSalary) {
}
