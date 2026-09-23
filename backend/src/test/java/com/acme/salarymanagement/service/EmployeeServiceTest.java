package com.acme.salarymanagement.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acme.salarymanagement.dto.EmployeeCreateRequest;
import com.acme.salarymanagement.dto.InitialSalaryRequest;
import com.acme.salarymanagement.exception.DuplicateEmployeeException;
import com.acme.salarymanagement.exception.EmployeeNotFoundException;
import com.acme.salarymanagement.model.Employee;
import com.acme.salarymanagement.model.EmploymentStatus;
import com.acme.salarymanagement.repository.EmployeeRepository;
import com.acme.salarymanagement.repository.SalaryRecordRepository;
import com.acme.salarymanagement.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private SalaryRecordRepository salaryRecordRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(employeeRepository, salaryRecordRepository, userRepository, passwordEncoder);
    }

    @Test
    void rejectsDuplicateEmployeeCodeBeforeWriting() {
        EmployeeCreateRequest request = request("EMP00001", "employee@example.test");
        when(employeeRepository.existsByEmployeeCodeIgnoreCase("EMP00001")).thenReturn(true);

        assertThatThrownBy(() -> employeeService.create(request))
                .isInstanceOf(DuplicateEmployeeException.class)
                .hasMessageContaining("employee code");
        verify(employeeRepository, never()).save(org.mockito.ArgumentMatchers.any(Employee.class));
    }

    @Test
    void reportsMissingEmployeeOnGet() {
        UUID id = UUID.randomUUID();
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.get(id))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    private EmployeeCreateRequest request(String employeeCode, String email) {
        return new EmployeeCreateRequest(
                employeeCode, "Test", "Employee", email, "US", "Engineering", "Software Engineer",
                EmploymentStatus.ACTIVE,
                new InitialSalaryRequest(BigDecimal.valueOf(100_000), "USD", LocalDate.of(2025, 1, 1)));
    }
}
