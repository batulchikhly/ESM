package com.acme.salarymanagement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.acme.salarymanagement.repository.DashboardRepository;
import com.acme.salarymanagement.repository.EmployeeRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DashboardServiceTest {

    @Test
    void summaryCombinesHeadcountAndCurrencyGroupedSalaryStatistics() {
        EmployeeRepository employees = Mockito.mock(EmployeeRepository.class);
        DashboardRepository dashboard = Mockito.mock(DashboardRepository.class);
        when(employees.count()).thenReturn(3L);
        when(employees.countByEmploymentStatus(com.acme.salarymanagement.model.EmploymentStatus.ACTIVE)).thenReturn(2L);
        when(employees.countByEmploymentStatus(com.acme.salarymanagement.model.EmploymentStatus.INACTIVE)).thenReturn(1L);
        when(dashboard.salaryStatisticsByCurrency(LocalDate.of(2026, 1, 1))).thenReturn(List.of(
                new CurrencyRow("USD", 2L), new CurrencyRow("INR", 1L)));

        DashboardService service = new DashboardService(employees, dashboard);
        var result = service.summary(LocalDate.of(2026, 1, 1));

        assertThat(result.totalEmployees()).isEqualTo(3);
        assertThat(result.activeEmployees()).isEqualTo(2);
        assertThat(result.inactiveEmployees()).isEqualTo(1);
        assertThat(result.salaryStatistics()).extracting("currency")
                .containsExactly("USD", "INR");
    }

    private record CurrencyRow(String currency, long employeeCount)
            implements com.acme.salarymanagement.repository.CurrencySalaryAggregate {
        @Override public String getCurrency() { return currency; }
        @Override public long getEmployeeCount() { return employeeCount; }
        @Override public java.math.BigDecimal getMinimumSalary() { return java.math.BigDecimal.ZERO; }
        @Override public java.math.BigDecimal getMaximumSalary() { return java.math.BigDecimal.ZERO; }
        @Override public java.math.BigDecimal getAverageSalary() { return java.math.BigDecimal.ZERO; }
    }
}
