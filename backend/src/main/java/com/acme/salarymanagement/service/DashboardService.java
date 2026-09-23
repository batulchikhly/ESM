package com.acme.salarymanagement.service;

import com.acme.salarymanagement.dto.CountryAnalyticsResponse;
import com.acme.salarymanagement.dto.DashboardSummaryResponse;
import com.acme.salarymanagement.dto.DepartmentAnalyticsResponse;
import com.acme.salarymanagement.dto.SalaryBandResponse;
import com.acme.salarymanagement.dto.SalaryStatisticsResponse;
import com.acme.salarymanagement.model.EmploymentStatus;
import com.acme.salarymanagement.repository.CountrySalaryAggregate;
import com.acme.salarymanagement.repository.CurrencySalaryAggregate;
import com.acme.salarymanagement.repository.DashboardRepository;
import com.acme.salarymanagement.repository.DepartmentSalaryAggregate;
import com.acme.salarymanagement.repository.EmployeeRepository;
import com.acme.salarymanagement.repository.SalaryBandAggregate;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private final EmployeeRepository employeeRepository;
    private final DashboardRepository dashboardRepository;

    public DashboardService(EmployeeRepository employeeRepository, DashboardRepository dashboardRepository) {
        this.employeeRepository = employeeRepository;
        this.dashboardRepository = dashboardRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse summary(LocalDate asOfDate) {
        return new DashboardSummaryResponse(
                employeeRepository.count(),
                employeeRepository.countByEmploymentStatus(EmploymentStatus.ACTIVE),
                employeeRepository.countByEmploymentStatus(EmploymentStatus.INACTIVE),
                dashboardRepository.salaryStatisticsByCurrency(asOfDate).stream()
                        .map(this::toStatistics)
                        .toList());
    }

    @Transactional(readOnly = true)
    public List<CountryAnalyticsResponse> byCountry(LocalDate asOfDate) {
        return dashboardRepository.salaryStatisticsByCountry(asOfDate).stream()
                .map(this::toCountry)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DepartmentAnalyticsResponse> byDepartment(LocalDate asOfDate) {
        return dashboardRepository.salaryStatisticsByDepartment(asOfDate).stream()
                .map(this::toDepartment)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SalaryBandResponse> salaryBands(LocalDate asOfDate) {
        return dashboardRepository.salaryBands(asOfDate).stream()
                .map(this::toBand)
                .toList();
    }

    private SalaryStatisticsResponse toStatistics(CurrencySalaryAggregate row) {
        return new SalaryStatisticsResponse(row.getCurrency(), row.getEmployeeCount(), row.getMinimumSalary(),
                row.getMaximumSalary(), row.getAverageSalary());
    }

    private CountryAnalyticsResponse toCountry(CountrySalaryAggregate row) {
        return new CountryAnalyticsResponse(row.getCountry(), row.getCurrency(), row.getEmployeeCount(),
                row.getMinimumSalary(), row.getMaximumSalary(), row.getAverageSalary());
    }

    private DepartmentAnalyticsResponse toDepartment(DepartmentSalaryAggregate row) {
        return new DepartmentAnalyticsResponse(row.getDepartment(), row.getCurrency(), row.getEmployeeCount(),
                row.getMinimumSalary(), row.getMaximumSalary(), row.getAverageSalary());
    }

    private SalaryBandResponse toBand(SalaryBandAggregate row) {
        return new SalaryBandResponse(row.getCurrency(), row.getBand(), row.getEmployeeCount());
    }
}