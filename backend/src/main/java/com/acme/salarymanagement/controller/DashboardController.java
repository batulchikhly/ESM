package com.acme.salarymanagement.controller;

import com.acme.salarymanagement.dto.CountryAnalyticsResponse;
import com.acme.salarymanagement.dto.DashboardSummaryResponse;
import com.acme.salarymanagement.dto.DepartmentAnalyticsResponse;
import com.acme.salarymanagement.dto.SalaryBandResponse;
import com.acme.salarymanagement.service.DashboardService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public DashboardSummaryResponse summary(@RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        return dashboardService.summary(asOfDate == null ? LocalDate.now() : asOfDate);
    }

    @GetMapping("/by-country")
    public List<CountryAnalyticsResponse> byCountry(@RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        return dashboardService.byCountry(asOfDate == null ? LocalDate.now() : asOfDate);
    }

    @GetMapping("/by-department")
    public List<DepartmentAnalyticsResponse> byDepartment(@RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        return dashboardService.byDepartment(asOfDate == null ? LocalDate.now() : asOfDate);
    }

    @GetMapping("/salary-bands")
    public List<SalaryBandResponse> salaryBands(@RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate) {
        return dashboardService.salaryBands(asOfDate == null ? LocalDate.now() : asOfDate);
    }
}