package com.acme.salarymanagement.seed;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;

class SeedDataGeneratorTest {

    private final SeedDataGenerator generator = new SeedDataGenerator();

    @Test
    void generatesExactlyTenThousandUniqueDeterministicEmployees() {
        List<SeedEmployeeData> first = generator.generate(10_000, 12345L);
        List<SeedEmployeeData> second = generator.generate(10_000, 12345L);

        assertThat(first).hasSize(10_000);
        assertThat(first).containsExactlyElementsOf(second);
        assertThat(first).extracting(SeedEmployeeData::employeeCode)
                .doesNotHaveDuplicates()
                .contains("EMP00001", "EMP10000");
        assertThat(first).extracting(SeedEmployeeData::email)
                .doesNotHaveDuplicates();
        assertThat(new HashSet<>(first.stream().map(SeedEmployeeData::country).toList()))
                .containsExactlyInAnyOrder("IN", "US", "GB", "DE", "SG", "AU");
        assertThat(first).extracting(SeedEmployeeData::currency)
                .contains("INR", "USD", "GBP", "EUR", "SGD", "AUD");
        assertThat(first).extracting(SeedEmployeeData::employmentStatus)
                .contains(com.acme.salarymanagement.employee.EmploymentStatus.ACTIVE,
                        com.acme.salarymanagement.employee.EmploymentStatus.INACTIVE);
    }
}
