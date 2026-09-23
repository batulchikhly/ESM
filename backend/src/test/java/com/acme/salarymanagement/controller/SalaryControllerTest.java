package com.acme.salarymanagement.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.acme.salarymanagement.common.error.GlobalExceptionHandler;
import com.acme.salarymanagement.dto.SalaryRecordResponse;
import com.acme.salarymanagement.dto.SalaryUpdateResponse;
import com.acme.salarymanagement.exception.EmployeeNotFoundException;
import com.acme.salarymanagement.service.SalaryService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SalaryController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class SalaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SalaryService salaryService;

    @Test
    void createsSalaryAndReturnsAuditId() throws Exception {
        UUID employeeId = UUID.randomUUID();
        SalaryRecordResponse salary = new SalaryRecordResponse(UUID.randomUUID(), employeeId,
                BigDecimal.valueOf(75_000), "USD", LocalDate.of(2026, 1, 1), Instant.parse("2026-01-01T00:00:00Z"),
                UUID.randomUUID());
        UUID auditId = UUID.randomUUID();
        when(salaryService.createSalary(eq(employeeId), any())).thenReturn(new SalaryUpdateResponse(salary, auditId));

        mockMvc.perform(post("/api/employees/{id}/salary", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"annualSalary":75000,"currency":"USD","effectiveFrom":"2026-01-01"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.salary.annualSalary").value(75000))
                .andExpect(jsonPath("$.salary.currency").value("USD"))
                .andExpect(jsonPath("$.auditId").value(auditId.toString()));
    }

    @Test
    void returnsSalaryHistory() throws Exception {
        UUID employeeId = UUID.randomUUID();
        when(salaryService.history(employeeId)).thenReturn(List.of(
                new SalaryRecordResponse(UUID.randomUUID(), employeeId, BigDecimal.valueOf(75_000), "USD",
                        LocalDate.of(2026, 1, 1), Instant.parse("2026-01-01T00:00:00Z"), UUID.randomUUID()),
                new SalaryRecordResponse(UUID.randomUUID(), employeeId, BigDecimal.valueOf(70_000), "USD",
                        LocalDate.of(2025, 1, 1), Instant.parse("2025-01-01T00:00:00Z"), UUID.randomUUID())));

        mockMvc.perform(get("/api/employees/{id}/salary-history", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].annualSalary").value(75000))
                .andExpect(jsonPath("$[1].annualSalary").value(70000));
    }

    @Test
    void returnsNotFoundWhenEmployeeDoesNotExist() throws Exception {
        UUID employeeId = UUID.randomUUID();
        when(salaryService.history(employeeId)).thenThrow(new EmployeeNotFoundException(employeeId));

        mockMvc.perform(get("/api/employees/{id}/salary-history", employeeId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("EMPLOYEE_NOT_FOUND"));
    }
}
