package com.acme.salarymanagement.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.acme.salarymanagement.dto.SalaryUpdateRequest;
import com.acme.salarymanagement.model.Employee;
import com.acme.salarymanagement.model.EmploymentStatus;
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
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.seed.enabled=false"
})
@ActiveProfiles("test")
class SalaryServiceTransactionIntegrationTest {

    @Autowired
    private SalaryService salaryService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private SalaryRecordRepository salaryRecordRepository;

    @Autowired
    private UserRepository userRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

    @Autowired
    private DataSource dataSource;

    @MockitoBean
    private AuditLogRepository auditLogRepository;

    private Employee employee;

    @BeforeEach
    void setUp() {
        User actor = userRepository.saveAndFlush(User.seedUser(
                "system@example.test", "hash", UserRole.HR_MANAGER, Instant.parse("2026-01-01T00:00:00Z")));
        employee = employeeRepository.saveAndFlush(Employee.seedEmployee(
                "EMP00001", "Test", "Employee", "employee@example.test", "US", "Engineering",
                "Software Engineer", EmploymentStatus.ACTIVE, Instant.parse("2026-01-01T00:00:00Z")));
        salaryRecordRepository.saveAndFlush(SalaryRecord.initialSalary(
                employee, BigDecimal.valueOf(70_000), "USD", LocalDate.of(2025, 1, 1),
                Instant.parse("2026-01-01T00:00:00Z"), actor));
        when(auditLogRepository.save(any())).thenThrow(new IllegalStateException("simulated audit failure"));
    }

    @Test
    void rollsBackSalaryWhenAuditCreationFails() {
        assertThatThrownBy(() -> salaryService.createSalary(employee.getId(),
                new SalaryUpdateRequest(BigDecimal.valueOf(75_000), "USD", LocalDate.of(2026, 1, 1))))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("simulated audit failure");

        List<SalaryRecord> history = salaryRecordRepository
                .findByEmployeeIdOrderByEffectiveFromAsc(employee.getId());

        org.springframework.jdbc.core.JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        Integer auditCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM audit_logs WHERE employee_id = ?",
                Integer.class,
                employee.getId());

        org.assertj.core.api.Assertions.assertThat(history).hasSize(1);

        SalaryRecord record = history.get(0);

        assertThat(record.getAnnualSalary())
                .isEqualByComparingTo("70000");

        assertThat(record.getCurrency())
                .isEqualTo("USD");
        org.assertj.core.api.Assertions.assertThat(auditCount).isZero();
    }
}
