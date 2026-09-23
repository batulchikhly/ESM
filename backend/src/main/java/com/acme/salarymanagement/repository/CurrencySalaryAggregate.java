package com.acme.salarymanagement.repository;

import java.math.BigDecimal;

public interface CurrencySalaryAggregate {
    String getCurrency();
    long getEmployeeCount();
    BigDecimal getMinimumSalary();
    BigDecimal getMaximumSalary();
    BigDecimal getAverageSalary();
}
