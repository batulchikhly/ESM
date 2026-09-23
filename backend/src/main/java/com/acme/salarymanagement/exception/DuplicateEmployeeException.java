package com.acme.salarymanagement.exception;

public class DuplicateEmployeeException extends RuntimeException {

    private final String field;

    public DuplicateEmployeeException(String field) {
        super("An employee with this " + field + " already exists");
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
