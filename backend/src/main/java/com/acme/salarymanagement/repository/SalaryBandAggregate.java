package com.acme.salarymanagement.repository;

public interface SalaryBandAggregate {
    String getCurrency();
    String getBand();
    long getEmployeeCount();
}
