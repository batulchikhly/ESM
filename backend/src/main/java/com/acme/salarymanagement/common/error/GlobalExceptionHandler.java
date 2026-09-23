package com.acme.salarymanagement.common.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import com.acme.salarymanagement.exception.DuplicateEmployeeException;
import com.acme.salarymanagement.exception.EmployeeNotFoundException;
import com.acme.salarymanagement.exception.EmployeeValidationException;
import com.acme.salarymanagement.exception.InvalidCredentialsException;
import com.acme.salarymanagement.exception.SalaryConflictException;
import com.acme.salarymanagement.exception.SalaryValidationException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        List<FieldErrorDetail> fieldErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toFieldError)
                .toList();
        return response(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Request validation failed", fieldErrors,
                request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request) {
        List<FieldErrorDetail> fieldErrors = exception.getConstraintViolations()
                .stream()
                .map(violation -> new FieldErrorDetail(violation.getPropertyPath().toString(), violation.getMessage()))
                .toList();
        return response(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Request validation failed", fieldErrors,
                request);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ApiError> handleEmployeeNotFound(
            EmployeeNotFoundException exception,
            HttpServletRequest request) {
        return response(HttpStatus.NOT_FOUND, "EMPLOYEE_NOT_FOUND", exception.getMessage(), List.of(), request);
    }

    @ExceptionHandler(DuplicateEmployeeException.class)
    public ResponseEntity<ApiError> handleDuplicateEmployee(
            DuplicateEmployeeException exception,
            HttpServletRequest request) {
        return response(HttpStatus.CONFLICT, "DUPLICATE_EMPLOYEE", exception.getMessage(),
                List.of(new FieldErrorDetail(exception.getField(), exception.getMessage())), request);
    }

    @ExceptionHandler(EmployeeValidationException.class)
    public ResponseEntity<ApiError> handleEmployeeValidation(
            EmployeeValidationException exception,
            HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", exception.getMessage(),
                List.of(new FieldErrorDetail(exception.getField(), exception.getMessage())), request);
    }

        @ExceptionHandler(SalaryValidationException.class)
        public ResponseEntity<ApiError> handleSalaryValidation(
                        SalaryValidationException exception,
                        HttpServletRequest request) {
                return response(HttpStatus.BAD_REQUEST, "SALARY_VALIDATION_ERROR", exception.getMessage(),
                                List.of(new FieldErrorDetail(exception.getField(), exception.getMessage())), request);
        }

        @ExceptionHandler(SalaryConflictException.class)
        public ResponseEntity<ApiError> handleSalaryConflict(
                        SalaryConflictException exception,
                        HttpServletRequest request) {
                return response(HttpStatus.CONFLICT, "SALARY_CONFLICT", exception.getMessage(), List.of(), request);
        }

        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<ApiError> handleInvalidCredentials(
                        InvalidCredentialsException exception,
                        HttpServletRequest request) {
                return response(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", exception.getMessage(), List.of(), request);
        }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER",
                "Invalid value for parameter: " + exception.getName(), List.of(), request);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<ApiError> handleUnreadableRequest(
            Exception exception,
            HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "Request body could not be parsed", List.of(),
                request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(
            DataIntegrityViolationException exception,
            HttpServletRequest request) {
        return response(HttpStatus.CONFLICT, "DATA_CONFLICT", "The request conflicts with existing data", List.of(),
                request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred", List.of(),
                request);
    }

    private FieldErrorDetail toFieldError(FieldError fieldError) {
        return new FieldErrorDetail(fieldError.getField(), fieldError.getDefaultMessage());
    }

    private ResponseEntity<ApiError> response(
            HttpStatus status,
            String code,
            String message,
            List<FieldErrorDetail> fieldErrors,
            HttpServletRequest request) {
        String correlationId = request.getHeader("X-Correlation-Id");
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }
        ApiError error = new ApiError(Instant.now(), status.value(), code, message,
                fieldErrors.stream().collect(Collectors.toList()), correlationId);
        return ResponseEntity.status(status).body(error);
    }
}
