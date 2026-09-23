package com.acme.salarymanagement.salary;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryRecordRepository extends JpaRepository<SalaryRecord, UUID> {

    List<SalaryRecord> findByEmployeeIdOrderByEffectiveFromDesc(UUID employeeId);

    List<SalaryRecord> findByEmployeeIdAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(
            UUID employeeId, LocalDate asOfDate);
}
