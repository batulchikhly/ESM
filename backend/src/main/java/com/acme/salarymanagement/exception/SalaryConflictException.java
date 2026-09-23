package com.acme.salarymanagement.exception;

public class SalaryConflictException extends RuntimeException {

    public SalaryConflictException(String message) {
        super(message);
    }
}