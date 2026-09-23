package com.acme.salarymanagement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acme.salarymanagement.dto.SalaryUpdateRequest;
import com.acme.salarymanagement.exception.EmployeeNotFoundException;
import com.acme.salarymanagement.exception.SalaryConflictException;
import com.acme.salarymanagement.exception.SalaryValidationException;
import com.acme.salarymanagement.model.AuditLog;
import com.acme.salarymanagement.model.Employee;
import com.acme.salarymanagement.model.EmploymentStatus;
import com.acme.salarymanagement.model.User;
import com.acme.salarymanagement.model.UserRole;
import com.acme.salarymanagement.repository.AuditLogRepository;
import com.acme.salarymanagement.repository.EmployeeRepository;
import com.acme.salarymanagement.repository.SalaryRecordRepository;
import com.acme.salarymanagement.repository.UserRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SalaryServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private SalaryRecordRepository salaryRecordRepository;
    @Mock
    private AuditLogRepository auditLogRepository;
    @Mock
    private UserRepository userRepository;

    private SalaryService salaryService;
    private UUID employeeId;
    private Employee employee;
    private User actor;

    @BeforeEach
    void setUp() {
        salaryService = new SalaryService(employeeRepository, salaryRecordRepository, auditLogRepository, userRepository);
        employeeId = UUID.randomUUID();
        employee = Employee.seedEmployee("EMP00001", "Test", "Employee", "employee@example.test", "US",
                "Engineering", "Software Engineer", EmploymentStatus.ACTIVE, Instant.parse("2026-01-01T00:00:00Z"));
        actor = User.seedUser("system@example.test", "hash", UserRole.HR_MANAGER,
                Instant.parse("2026-01-01T00:00:00Z"));
    }

    @Test
    void createsNewSalaryPreservesHistoryAndCreatesAudit() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        var existing = com.acme.salarymanagement.model.SalaryRecord.initialSalary(employee,
                BigDecimal.valueOf(70_000), "USD", LocalDate.of(2025, 1, 1), Instant.now(), actor);
        when(salaryRecordRepository.findByEmployeeIdOrderByEffectiveFromAsc(employeeId)).thenReturn(List.of(existing));
        when(userRepository.findByEmailIgnoreCase("system@example.test")).thenReturn(Optional.of(actor));
        when(salaryRecordRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(auditLogRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = salaryService.createSalary(employeeId,
                new SalaryUpdateRequest(BigDecimal.valueOf(75_000), "USD", LocalDate.of(2026, 1, 1)));

        assertThat(result.salary().annualSalary()).isEqualByComparingTo("75000");
        assertThat(result.salary().currency()).isEqualTo("USD");
        verify(salaryRecordRepository).save(any());
        verify(auditLogRepository).save(any(AuditLog.class));
        verify(salaryRecordRepository, never()).delete(any());
    }

    @Test
    void rejectsDuplicateEffectiveDateWithoutWriting() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        var existing = com.acme.salarymanagement.model.SalaryRecord.initialSalary(employee,
                BigDecimal.valueOf(70_000), "USD", LocalDate.of(2025, 1, 1), Instant.now(), actor);
        when(salaryRecordRepository.findByEmployeeIdOrderByEffectiveFromAsc(employeeId)).thenReturn(List.of(existing));

        assertThatThrownBy(() -> salaryService.createSalary(employeeId,
                new SalaryUpdateRequest(BigDecimal.valueOf(75_000), "USD", LocalDate.of(2025, 1, 1))))
                .isInstanceOf(SalaryConflictException.class);
        verify(salaryRecordRepository, never()).save(any());
        verify(auditLogRepository, never()).save(any());
    }

    @Test
    void rejectsInvalidSalaryAndCurrency() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> salaryService.createSalary(employeeId,
                new SalaryUpdateRequest(BigDecimal.valueOf(-1), "USD", LocalDate.of(2026, 1, 1))))
                .isInstanceOf(SalaryValidationException.class);
        assertThatThrownBy(() -> salaryService.createSalary(employeeId,
                new SalaryUpdateRequest(BigDecimal.valueOf(75_000), "XXX", LocalDate.of(2026, 1, 1))))
                .isInstanceOf(SalaryValidationException.class);
    }

    @Test
    void reportsMissingEmployee() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salaryService.createSalary(employeeId,
                new SalaryUpdateRequest(BigDecimal.valueOf(75_000), "USD", LocalDate.of(2026, 1, 1))))
                .isInstanceOf(EmployeeNotFoundException.class);
    }
}
