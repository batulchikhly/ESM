package com.acme.salarymanagement.repository;

import java.math.BigDecimal;

public interface CountrySalaryAggregate {
    String getCountry();
    String getCurrency();
    long getEmployeeCount();
    BigDecimal getMinimumSalary();
    BigDecimal getMaximumSalary();
    BigDecimal getAverageSalary();
}
