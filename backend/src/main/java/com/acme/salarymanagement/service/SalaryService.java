package com.acme.salarymanagement.service;

import com.acme.salarymanagement.dto.SalaryRecordResponse;
import com.acme.salarymanagement.dto.SalaryUpdateRequest;
import com.acme.salarymanagement.dto.SalaryUpdateResponse;
import com.acme.salarymanagement.exception.EmployeeNotFoundException;
import com.acme.salarymanagement.exception.SalaryConflictException;
import com.acme.salarymanagement.exception.SalaryValidationException;
import com.acme.salarymanagement.model.AuditAction;
import com.acme.salarymanagement.model.AuditLog;
import com.acme.salarymanagement.model.Employee;
import com.acme.salarymanagement.model.SalaryRecord;
import com.acme.salarymanagement.model.User;
import com.acme.salarymanagement.model.UserRole;
import com.acme.salarymanagement.repository.AuditLogRepository;
import com.acme.salarymanagement.repository.EmployeeRepository;
import com.acme.salarymanagement.repository.SalaryRecordRepository;
import com.acme.salarymanagement.repository.UserRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SalaryService {

    private static final Set<String> SUPPORTED_CURRENCIES = Set.of("USD", "EUR", "GBP", "INR", "SGD", "AUD");
    private static final String SYSTEM_USER_EMAIL = "system@example.test";
    private static final String SYSTEM_PASSWORD = "internal-system-user-not-for-login";

    private final EmployeeRepository employeeRepository;
    private final SalaryRecordRepository salaryRecordRepository;
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SalaryService(
            EmployeeRepository employeeRepository,
            SalaryRecordRepository salaryRecordRepository,
            AuditLogRepository auditLogRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.salaryRecordRepository = salaryRecordRepository;
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public SalaryUpdateResponse createSalary(UUID employeeId, SalaryUpdateRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        validate(request);

        List<SalaryRecord> history = salaryRecordRepository
                .findByEmployeeIdOrderByEffectiveFromAsc(employeeId);
        if (history.stream().anyMatch(record -> record.getEffectiveFrom().equals(request.effectiveFrom()))) {
            throw new SalaryConflictException("A salary record already exists for this effective date");
        }

        SalaryRecord previous = history.stream()
                .filter(record -> !record.getEffectiveFrom().isAfter(request.effectiveFrom()))
                .max(Comparator.comparing(SalaryRecord::getEffectiveFrom))
                .orElse(null);
        Instant changedAt = Instant.now();
        User actor = systemUser(changedAt);
        SalaryRecord salary = salaryRecordRepository.save(SalaryRecord.initialSalary(
                employee,
                request.annualSalary(),
                request.currency().trim().toUpperCase(),
                request.effectiveFrom(),
                changedAt,
                actor));
        AuditLog audit = auditLogRepository.save(AuditLog.salaryChange(
                employee,
                previous == null ? AuditAction.SALARY_CREATED : AuditAction.SALARY_UPDATED,
                value(previous),
                value(salary),
                actor,
                changedAt));
        try {
            salaryRecordRepository.flush();
            auditLogRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new SalaryConflictException("A salary record already exists for this effective date");
        }
        return new SalaryUpdateResponse(toResponse(salary), audit.getId());
    }

    @Transactional(readOnly = true)
    public List<SalaryRecordResponse> history(UUID employeeId) {
        employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        return salaryRecordRepository.findByEmployeeIdOrderByEffectiveFromDesc(employeeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void validate(SalaryUpdateRequest request) {
        BigDecimal salary = request.annualSalary();
        if (salary == null || salary.signum() < 0) {
            throw new SalaryValidationException("annualSalary", "Annual salary must be greater than or equal to zero");
        }
        if (request.currency() == null || !SUPPORTED_CURRENCIES.contains(request.currency().trim().toUpperCase())) {
            throw new SalaryValidationException("currency", "Unsupported currency code");
        }
        if (request.effectiveFrom() == null) {
            throw new SalaryValidationException("effectiveFrom", "Effective date is required");
        }
    }

    private User systemUser(Instant timestamp) {
        return userRepository.findByEmailIgnoreCase(SYSTEM_USER_EMAIL)
                .orElseGet(() -> userRepository.save(User.seedUser(
                    SYSTEM_USER_EMAIL, passwordEncoder.encode(SYSTEM_PASSWORD), UserRole.HR_MANAGER, timestamp)));
    }

    private String value(SalaryRecord record) {
        return record == null ? null : record.getAnnualSalary() + " " + record.getCurrency()
                + " effective " + record.getEffectiveFrom();
    }

    private SalaryRecordResponse toResponse(SalaryRecord record) {
        return new SalaryRecordResponse(record.getId(), record.getEmployee().getId(), record.getAnnualSalary(),
                record.getCurrency(), record.getEffectiveFrom(), record.getCreatedAt(), record.getCreatedBy().getId());
    }
}
