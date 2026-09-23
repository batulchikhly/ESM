package com.acme.salarymanagement.repository;

import com.acme.salarymanagement.model.Employee;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EmployeeRepository extends JpaRepository<Employee, UUID>, JpaSpecificationExecutor<Employee> {

	boolean existsByEmployeeCodeIgnoreCase(String employeeCode);

	boolean existsByEmailIgnoreCase(String email);

	boolean existsByEmployeeCodeIgnoreCaseAndIdNot(String employeeCode, UUID id);

	boolean existsByEmailIgnoreCaseAndIdNot(String email, UUID id);
}
