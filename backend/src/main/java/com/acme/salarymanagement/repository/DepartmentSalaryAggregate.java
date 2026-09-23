package com.acme.salarymanagement.repository;

import java.math.BigDecimal;

public interface DepartmentSalaryAggregate {
    String getDepartment();
    String getCurrency();
    long getEmployeeCount();
    BigDecimal getMinimumSalary();
    BigDecimal getMaximumSalary();
    BigDecimal getAverageSalary();
}
