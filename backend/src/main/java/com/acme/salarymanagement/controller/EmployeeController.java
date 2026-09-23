package com.acme.salarymanagement.controller;

import com.acme.salarymanagement.dto.EmployeeCreateRequest;
import com.acme.salarymanagement.dto.EmployeePageResponse;
import com.acme.salarymanagement.dto.EmployeeResponse;
import com.acme.salarymanagement.dto.EmployeeUpdateRequest;
import com.acme.salarymanagement.exception.EmployeeValidationException;
import com.acme.salarymanagement.model.EmploymentStatus;
import com.acme.salarymanagement.service.EmployeeService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private static final int DEFAULT_PAGE_SIZE = 25;
    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "employeeCode", "firstName", "lastName", "country", "department", "employmentStatus", "createdAt");

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public EmployeePageResponse list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "lastName,asc") String sort,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String currency,
            @RequestParam(name = "status", required = false) EmploymentStatus employmentStatus,
            @RequestParam(required = false) BigDecimal minSalary,
            @RequestParam(required = false) BigDecimal maxSalary) {
        if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) {
            throw new EmployeeValidationException("page", "Page must be non-negative and size must be between 1 and 100");
        }
        return employeeService.list(search, country, department, currency, employmentStatus, minSalary, maxSalary,
                PageRequest.of(page, size, parseSort(sort)));
    }

    @GetMapping("/{id}")
    public EmployeeResponse get(@PathVariable UUID id) {
        return employeeService.get(id);
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.create(request));
    }

    @PutMapping("/{id}")
    public EmployeeResponse update(@PathVariable UUID id, @Valid @RequestBody EmployeeUpdateRequest request) {
        return employeeService.update(id, request);
    }

    @PatchMapping("/{id}/deactivate")
    public EmployeeResponse deactivate(@PathVariable UUID id) {
        return employeeService.deactivate(id);
    }

    private Sort parseSort(String sort) {
        String[] parts = sort.split(",", -1);
        if (parts.length != 2 || !SORTABLE_FIELDS.contains(parts[0])) {
            throw new EmployeeValidationException("sort", "Unsupported sort field");
        }
        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(parts[1]);
        } catch (IllegalArgumentException exception) {
            throw new EmployeeValidationException("sort", "Sort direction must be asc or desc");
        }
        return Sort.by(direction, parts[0]);
    }
}