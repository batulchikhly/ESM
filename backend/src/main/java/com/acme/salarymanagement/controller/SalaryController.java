package com.acme.salarymanagement.controller;

import com.acme.salarymanagement.dto.SalaryRecordResponse;
import com.acme.salarymanagement.dto.SalaryUpdateRequest;
import com.acme.salarymanagement.dto.SalaryUpdateResponse;
import com.acme.salarymanagement.service.SalaryService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees/{employeeId}")
public class SalaryController {

    private final SalaryService salaryService;

    public SalaryController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    @PostMapping("/salary")
    public ResponseEntity<SalaryUpdateResponse> createSalary(
            @PathVariable UUID employeeId,
            @Valid @RequestBody SalaryUpdateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salaryService.createSalary(employeeId, request));
    }

    @GetMapping("/salary-history")
    public List<SalaryRecordResponse> history(@PathVariable UUID employeeId) {
        return salaryService.history(employeeId);
    }
}
