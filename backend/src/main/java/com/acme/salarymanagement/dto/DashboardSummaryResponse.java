package com.acme.salarymanagement.dto;

import java.util.List;

public record DashboardSummaryResponse(
        long totalEmployees,
        long activeEmployees,
        long inactiveEmployees,
        List<SalaryStatisticsResponse> salaryStatistics) {
}
