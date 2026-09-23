package com.acme.salarymanagement.service;

import com.acme.salarymanagement.dto.EmployeeCreateRequest;
import com.acme.salarymanagement.dto.EmployeePageResponse;
import com.acme.salarymanagement.dto.EmployeeResponse;
import com.acme.salarymanagement.dto.EmployeeUpdateRequest;
import com.acme.salarymanagement.dto.SalarySummary;
import com.acme.salarymanagement.exception.DuplicateEmployeeException;
import com.acme.salarymanagement.exception.EmployeeNotFoundException;
import com.acme.salarymanagement.exception.EmployeeValidationException;
import com.acme.salarymanagement.model.Employee;
import com.acme.salarymanagement.model.EmploymentStatus;
import com.acme.salarymanagement.model.SalaryRecord;
import com.acme.salarymanagement.model.User;
import com.acme.salarymanagement.model.UserRole;
import com.acme.salarymanagement.repository.EmployeeRepository;
import com.acme.salarymanagement.repository.EmployeeSpecifications;
import com.acme.salarymanagement.repository.SalaryRecordRepository;
import com.acme.salarymanagement.repository.UserRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

    private static final Set<String> SUPPORTED_CURRENCIES = Set.of("USD", "EUR", "GBP", "INR", "SGD", "AUD");
    private static final String SYSTEM_USER_EMAIL = "system@example.test";
    private static final String SYSTEM_PASSWORD = "internal-system-user-not-for-login";

    private final EmployeeRepository employeeRepository;
    private final SalaryRecordRepository salaryRecordRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            SalaryRecordRepository salaryRecordRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.salaryRecordRepository = salaryRecordRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public EmployeePageResponse list(
            String search,
            String country,
            String department,
            String currency,
            EmploymentStatus employmentStatus,
            java.math.BigDecimal minimumSalary,
            java.math.BigDecimal maximumSalary,
            Pageable pageable) {
        validateSalaryRange(minimumSalary, maximumSalary);
            validateCurrencyFilter(currency);
            Specification<Employee> specification = Specification.allOf(Stream.of(
                EmployeeSpecifications.search(search),
                EmployeeSpecifications.country(country),
                EmployeeSpecifications.department(department),
                EmployeeSpecifications.employmentStatus(employmentStatus),
                EmployeeSpecifications.currentSalary(currency, minimumSalary, maximumSalary, LocalDate.now()))
                .filter(java.util.Objects::nonNull)
                .toList());
        Page<Employee> page = employeeRepository.findAll(specification, pageable);
        return toPageResponse(page, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public EmployeeResponse get(UUID id) {
        Employee employee = findEmployee(id);
        return toResponse(employee, currentSalary(employee.getId(), LocalDate.now()));
    }

    @Transactional
    public EmployeeResponse create(EmployeeCreateRequest request) {
        validateSupportedCurrency(request.initialSalary().currency());
        if (employeeRepository.existsByEmployeeCodeIgnoreCase(request.employeeCode())) {
            throw new DuplicateEmployeeException("employee code");
        }
        if (employeeRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateEmployeeException("email");
        }

        Employee employee = employeeRepository.save(Employee.create(
                request.employeeCode().trim(), request.firstName().trim(), request.lastName().trim(),
                request.email().trim(), request.country().trim().toUpperCase(), request.department().trim(),
                request.jobTitle().trim(), request.employmentStatus()));
        User actor = systemUser();
        salaryRecordRepository.save(SalaryRecord.initialSalary(
                employee,
                request.initialSalary().annualSalary(),
                request.initialSalary().currency().trim().toUpperCase(),
                request.initialSalary().effectiveFrom(),
                Instant.now(),
                actor));
        return get(employee.getId());
    }

    @Transactional
    public EmployeeResponse update(UUID id, EmployeeUpdateRequest request) {
        Employee employee = findEmployee(id);
        if (employeeRepository.existsByEmailIgnoreCaseAndIdNot(request.email(), id)) {
            throw new DuplicateEmployeeException("email");
        }
        employee.updateProfile(
                request.firstName().trim(), request.lastName().trim(), request.email().trim(),
                request.country().trim().toUpperCase(), request.department().trim(), request.jobTitle().trim(),
                request.employmentStatus());
        return toResponse(employeeRepository.save(employee), currentSalary(id, LocalDate.now()));
    }

    @Transactional
    public EmployeeResponse deactivate(UUID id) {
        Employee employee = findEmployee(id);
        employee.deactivate();
        return toResponse(employeeRepository.save(employee), currentSalary(id, LocalDate.now()));
    }

    private Employee findEmployee(UUID id) {
        return employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    private EmployeePageResponse toPageResponse(Page<Employee> page, LocalDate asOfDate) {
        List<UUID> ids = page.getContent().stream().map(Employee::getId).toList();
        Map<UUID, SalaryRecord> salaries = currentSalaries(ids, asOfDate);
        List<EmployeeResponse> content = page.getContent().stream()
                .map(employee -> toResponse(employee, salaries.get(employee.getId())))
                .toList();
        return new EmployeePageResponse(content, page.getNumber(), page.getSize(), page.getTotalElements(),
                page.getTotalPages());
    }

    private Map<UUID, SalaryRecord> currentSalaries(List<UUID> employeeIds, LocalDate asOfDate) {
        if (employeeIds.isEmpty()) {
            return Map.of();
        }
        Map<UUID, SalaryRecord> salaries = new HashMap<>();
        salaryRecordRepository.findByEmployee_IdInAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(
                        employeeIds, asOfDate)
                .forEach(record -> salaries.putIfAbsent(record.getEmployee().getId(), record));
        return salaries;
    }

    private SalaryRecord currentSalary(UUID employeeId, LocalDate asOfDate) {
        return salaryRecordRepository
                .findByEmployeeIdAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(employeeId, asOfDate)
                .stream()
                .findFirst()
                .orElse(null);
    }

    private EmployeeResponse toResponse(Employee employee, SalaryRecord salary) {
        SalarySummary summary = salary == null
                ? null
                : new SalarySummary(salary.getAnnualSalary(), salary.getCurrency(), salary.getEffectiveFrom());
        return new EmployeeResponse(employee.getId(), employee.getEmployeeCode(), employee.getFirstName(),
                employee.getLastName(), employee.getEmail(), employee.getCountry(), employee.getDepartment(),
                employee.getJobTitle(), employee.getEmploymentStatus(), employee.getCreatedAt(), employee.getUpdatedAt(),
                summary);
    }

    private User systemUser() {
        return userRepository.findByEmailIgnoreCase(SYSTEM_USER_EMAIL)
                .orElseGet(() -> userRepository.save(User.seedUser(
                    SYSTEM_USER_EMAIL, passwordEncoder.encode(SYSTEM_PASSWORD), UserRole.HR_MANAGER, Instant.now())));
    }

    private void validateSupportedCurrency(String currency) {
        if (currency == null || !SUPPORTED_CURRENCIES.contains(currency.trim().toUpperCase())) {
            throw new EmployeeValidationException("initialSalary.currency", "Unsupported currency code");
        }
    }

    private void validateCurrencyFilter(String currency) {
        if (currency != null && !currency.isBlank()
                && !SUPPORTED_CURRENCIES.contains(currency.trim().toUpperCase())) {
            throw new EmployeeValidationException("currency", "Unsupported currency code");
        }
    }

    private void validateSalaryRange(java.math.BigDecimal minimumSalary, java.math.BigDecimal maximumSalary) {
        if (minimumSalary != null && minimumSalary.signum() < 0
                || maximumSalary != null && maximumSalary.signum() < 0) {
            throw new EmployeeValidationException("salary", "Salary range cannot be negative");
        }
        if (minimumSalary != null && maximumSalary != null && minimumSalary.compareTo(maximumSalary) > 0) {
            throw new EmployeeValidationException("salary", "Minimum salary cannot exceed maximum salary");
        }
    }
}
