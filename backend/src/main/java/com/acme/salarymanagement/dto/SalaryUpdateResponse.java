package com.acme.salarymanagement.dto;

import java.util.UUID;

public record SalaryUpdateResponse(SalaryRecordResponse salary, UUID auditId) {
}