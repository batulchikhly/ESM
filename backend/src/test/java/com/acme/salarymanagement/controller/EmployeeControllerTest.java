package com.acme.salarymanagement.controller;

import com.acme.salarymanagement.common.error.GlobalExceptionHandler;
import com.acme.salarymanagement.dto.EmployeePageResponse;
import com.acme.salarymanagement.exception.EmployeeNotFoundException;
import com.acme.salarymanagement.service.EmployeeService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import com.acme.salarymanagement.config.JwtService;
import com.acme.salarymanagement.repository.UserRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void listsOnlyRequestedPageAndPassesSafeSort() throws Exception {
        when(employeeService.list(eq("smith"), eq("US"), eq("Engineering"), eq(null), eq(null), eq(null), eq(null), any()))
                .thenReturn(new EmployeePageResponse(List.of(), 1, 25, 10_000, 400));

        mockMvc.perform(get("/api/employees")
                        .param("page", "1")
                        .param("size", "25")
                        .param("sort", "lastName,asc")
                        .param("search", "smith")
                        .param("country", "US")
                        .param("department", "Engineering"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(25))
                .andExpect(jsonPath("$.totalElements").value(10_000));

        verify(employeeService).list(eq("smith"), eq("US"), eq("Engineering"), eq(null), eq(null), eq(null), eq(null), any());
    }

    @Test
    void rejectsUnsafeSortField() throws Exception {
        mockMvc.perform(get("/api/employees").param("sort", "salary;drop table employees,asc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void returnsNotFoundForMissingEmployee() throws Exception {
        UUID id = UUID.randomUUID();
        when(employeeService.get(id)).thenThrow(new EmployeeNotFoundException(id));

        mockMvc.perform(get("/api/employees/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("EMPLOYEE_NOT_FOUND"));
    }
}
