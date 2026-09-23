package com.acme.salarymanagement.seed;

import com.acme.salarymanagement.auth.User;
import com.acme.salarymanagement.auth.UserRepository;
import com.acme.salarymanagement.auth.UserRole;
import com.acme.salarymanagement.employee.Employee;
import com.acme.salarymanagement.employee.EmployeeRepository;
import com.acme.salarymanagement.salary.SalaryRecord;
import com.acme.salarymanagement.salary.SalaryRecordRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeedService {

    public static final String SEED_NAME = "synthetic-employees";
    public static final String SEED_USER_EMAIL = "seed-admin@example.test";
    private static final String SEED_PASSWORD_HASH = "{noop}seed-password-not-for-authentication";
    private static final Instant SEED_TIMESTAMP = Instant.parse("2026-01-01T00:00:00Z");
    private static final LocalDate INITIAL_EFFECTIVE_DATE = LocalDate.of(2025, 1, 1);

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final SalaryRecordRepository salaryRecordRepository;
    private final SeedRunRepository seedRunRepository;
    private final SeedDataGenerator dataGenerator;
    private final SeedProperties properties;

    public SeedService(
            UserRepository userRepository,
            EmployeeRepository employeeRepository,
            SalaryRecordRepository salaryRecordRepository,
            SeedRunRepository seedRunRepository,
            SeedDataGenerator dataGenerator,
            SeedProperties properties) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.salaryRecordRepository = salaryRecordRepository;
        this.seedRunRepository = seedRunRepository;
        this.dataGenerator = dataGenerator;
        this.properties = properties;
    }

    @Transactional
    public SeedResult seed() {
        if (seedAlreadyCompleted()) {
            return new SeedResult(false, 0, 0);
        }
        if (employeeRepository.count() > 0 || salaryRecordRepository.count() > 0) {
            throw new IllegalStateException("Seed refused because application data already exists");
        }

        User seedUser = userRepository.findByEmailIgnoreCase(SEED_USER_EMAIL)
                .orElseGet(() -> userRepository.save(User.seedUser(
                        SEED_USER_EMAIL, SEED_PASSWORD_HASH, UserRole.HR_MANAGER, SEED_TIMESTAMP)));

        List<SeedEmployeeData> data = dataGenerator.generate(properties.employeeCount(), properties.randomSeed());
        Map<String, SeedEmployeeData> dataByEmployeeCode = data.stream()
                .collect(Collectors.toMap(SeedEmployeeData::employeeCode, Function.identity()));
        List<Employee> employees = data.stream()
                .map(item -> Employee.seedEmployee(
                        item.employeeCode(), item.firstName(), item.lastName(), item.email(), item.country(),
                        item.department(), item.jobTitle(), item.employmentStatus(), SEED_TIMESTAMP))
                .toList();
        List<Employee> savedEmployees = employeeRepository.saveAll(employees);
        List<SalaryRecord> salaryRecords = savedEmployees.stream()
                .map(employee -> {
                    SeedEmployeeData item = dataByEmployeeCode.get(employee.getEmployeeCode());
                    return SalaryRecord.initialSalary(employee, item.annualSalary(), item.currency(),
                            INITIAL_EFFECTIVE_DATE, SEED_TIMESTAMP, seedUser);
                })
                .toList();
        salaryRecordRepository.saveAll(salaryRecords);

        seedRunRepository.save(SeedRun.completed(SEED_NAME, properties.version(), SEED_TIMESTAMP));
        return new SeedResult(true, savedEmployees.size(), salaryRecords.size());
    }

    private boolean seedAlreadyCompleted() {
        return seedRunRepository.findBySeedNameAndSeedVersion(SEED_NAME, properties.version()).isPresent();
    }

    public record SeedResult(boolean seeded, int employeeCount, int salaryRecordCount) {
    }
}
