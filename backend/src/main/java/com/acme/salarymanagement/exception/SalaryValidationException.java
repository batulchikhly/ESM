package com.acme.salarymanagement.exception;

public class SalaryValidationException extends RuntimeException {

    private final String field;

    public SalaryValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}