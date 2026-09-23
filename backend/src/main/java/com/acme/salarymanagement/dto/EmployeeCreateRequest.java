package com.acme.salarymanagement.dto;

import com.acme.salarymanagement.model.EmploymentStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmployeeCreateRequest(
        @NotBlank @Pattern(regexp = "EMP\\d{5,}", message = "Must match EMP followed by at least five digits")
        String employeeCode,
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotBlank @Email @Size(max = 320) String email,
        @NotBlank @Pattern(regexp = "[A-Z]{2}", message = "Must be an ISO 3166-1 alpha-2 country code")
        String country,
        @NotBlank @Size(max = 100) String department,
        @NotBlank @Size(max = 150) String jobTitle,
        @NotNull EmploymentStatus employmentStatus,
        @NotNull @Valid InitialSalaryRequest initialSalary) {
}
