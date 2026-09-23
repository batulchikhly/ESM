package com.acme.salarymanagement.repository;

import com.acme.salarymanagement.model.SalaryRecord;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryRecordRepository extends JpaRepository<SalaryRecord, UUID> {

        List<SalaryRecord> findByEmployee_IdInAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(
            List<UUID> employeeIds, LocalDate asOfDate);

    List<SalaryRecord> findByEmployeeIdOrderByEffectiveFromDesc(UUID employeeId);

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        List<SalaryRecord> findByEmployeeIdOrderByEffectiveFromAsc(UUID employeeId);

    List<SalaryRecord> findByEmployeeIdAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(
            UUID employeeId, LocalDate asOfDate);
}
