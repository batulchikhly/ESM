package com.acme.salarymanagement.seed;

import com.acme.salarymanagement.employee.EmploymentStatus;
import java.math.BigDecimal;

public record SeedEmployeeData(
        String employeeCode,
        String firstName,
        String lastName,
        String email,
        String country,
        String department,
        String jobTitle,
        EmploymentStatus employmentStatus,
        BigDecimal annualSalary,
        String currency) {
}
