package com.acme.salarymanagement.seed;

import com.acme.salarymanagement.model.EmploymentStatus;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SeedDataGenerator {

    private static final List<CountryProfile> COUNTRIES = List.of(
            new CountryProfile("IN", "INR", 900_000, 3_500_000),
            new CountryProfile("US", "USD", 55_000, 180_000),
            new CountryProfile("GB", "GBP", 30_000, 110_000),
            new CountryProfile("DE", "EUR", 38_000, 125_000),
            new CountryProfile("SG", "SGD", 48_000, 160_000),
            new CountryProfile("AU", "AUD", 52_000, 170_000));

    private static final List<String> DEPARTMENTS = List.of(
            "Engineering", "Finance", "HR", "Sales", "Marketing", "Operations", "Legal", "Support");

    private static final List<String> JOB_TITLES = List.of(
            "Software Engineer", "Senior Software Engineer", "Engineering Manager", "Product Manager",
            "HR Manager", "Financial Analyst", "Sales Manager", "Marketing Specialist",
            "Operations Manager", "Support Specialist");

    private static final List<String> FIRST_NAMES = List.of(
            "Aarav", "Maya", "Liam", "Sofia", "Noah", "Amelia", "Oliver", "Fatima", "Ethan", "Zoe");

    private static final List<String> LAST_NAMES = List.of(
            "Adler", "Bennett", "Chandra", "Dawson", "Evans", "Fischer", "Gupta", "Hughes", "Iyer", "Khan");

    public List<SeedEmployeeData> generate(int count, long randomSeed) {
        if (count < 0) {
            throw new IllegalArgumentException("Seed employee count cannot be negative");
        }

        Random random = new Random(randomSeed);
        List<SeedEmployeeData> employees = new ArrayList<>(count);
        for (int index = 1; index <= count; index++) {
            CountryProfile country = COUNTRIES.get(random.nextInt(COUNTRIES.size()));
            String department = DEPARTMENTS.get(random.nextInt(DEPARTMENTS.size()));
            String jobTitle = JOB_TITLES.get(random.nextInt(JOB_TITLES.size()));
            String firstName = FIRST_NAMES.get(random.nextInt(FIRST_NAMES.size()));
            String lastName = LAST_NAMES.get(random.nextInt(LAST_NAMES.size()));
            long salaryRange = country.maximumSalary() - country.minimumSalary();
            long salary = country.minimumSalary() + random.nextLong(salaryRange + 1);
            EmploymentStatus status = random.nextInt(10) == 0
                    ? EmploymentStatus.INACTIVE
                    : EmploymentStatus.ACTIVE;
            String employeeCode = "EMP%05d".formatted(index);

            employees.add(new SeedEmployeeData(
                    employeeCode,
                    firstName,
                    lastName,
                    "employee%05d@example.test".formatted(index),
                    country.code(),
                    department,
                    jobTitle,
                    status,
                    BigDecimal.valueOf(salary),
                    country.currency()));
        }
        return List.copyOf(employees);
    }

    private record CountryProfile(String code, String currency, long minimumSalary, long maximumSalary) {
    }
}
