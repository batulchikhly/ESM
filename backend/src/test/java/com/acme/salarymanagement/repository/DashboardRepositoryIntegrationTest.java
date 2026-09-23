package com.acme.salarymanagement.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.acme.salarymanagement.model.Employee;
import com.acme.salarymanagement.model.EmploymentStatus;
import com.acme.salarymanagement.model.SalaryRecord;
import com.acme.salarymanagement.model.User;
import com.acme.salarymanagement.model.UserRole;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@ActiveProfiles("test")
class DashboardRepositoryIntegrationTest {

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private SalaryRecordRepository salaryRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user = userRepository.save(User.seedUser(
                "dashboard@example.test", "hash", UserRole.HR_MANAGER, Instant.parse("2026-01-01T00:00:00Z")));
        Employee activeUs = employeeRepository.save(Employee.seedEmployee(
                "EMP00001", "Active", "US", "active-us@example.test", "US", "Engineering", "Engineer",
                EmploymentStatus.ACTIVE, Instant.parse("2026-01-01T00:00:00Z")));
        Employee inactiveIndia = employeeRepository.save(Employee.seedEmployee(
                "EMP00002", "Inactive", "India", "inactive-in@example.test", "IN", "Finance", "Analyst",
                EmploymentStatus.INACTIVE, Instant.parse("2026-01-01T00:00:00Z")));
        salaryRecordRepository.save(SalaryRecord.initialSalary(activeUs, BigDecimal.valueOf(70_000), "USD",
                LocalDate.of(2025, 1, 1), Instant.parse("2026-01-01T00:00:00Z"), user));
        salaryRecordRepository.save(SalaryRecord.initialSalary(activeUs, BigDecimal.valueOf(75_000), "USD",
                LocalDate.of(2026, 1, 1), Instant.parse("2026-01-01T00:00:00Z"), user));
        salaryRecordRepository.save(SalaryRecord.initialSalary(inactiveIndia, BigDecimal.valueOf(3_000_000), "INR",
                LocalDate.of(2025, 1, 1), Instant.parse("2026-01-01T00:00:00Z"), user));
        salaryRecordRepository.flush();
    }

    @Test
    void aggregatesOnlyTheLatestSalaryPerEmployeeAndKeepsCurrencyGroupsSeparate() {
        var rows = dashboardRepository.salaryStatisticsByCurrency(LocalDate.of(2026, 6, 1));

        assertThat(rows).hasSize(2);
        assertThat(rows).extracting(CurrencySalaryAggregate::getCurrency)
                .containsExactly("INR", "USD");
        CurrencySalaryAggregate usd = rows.stream().filter(row -> row.getCurrency().equals("USD")).findFirst().orElseThrow();
        assertThat(usd.getEmployeeCount()).isEqualTo(1);
        assertThat(usd.getMinimumSalary()).isEqualByComparingTo("75000");
    }

    @Test
    void groupsCountryAndSalaryBandsFromCurrentRecords() {
        var countries = dashboardRepository.salaryStatisticsByCountry(LocalDate.of(2026, 6, 1));
        var bands = dashboardRepository.salaryBands(LocalDate.of(2026, 6, 1));

        assertThat(countries).extracting(CountrySalaryAggregate::getCountry)
                .containsExactly("IN", "US");
        assertThat(bands).extracting(SalaryBandAggregate::getCurrency)
                .contains("INR", "USD");
    }
}
