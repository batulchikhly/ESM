package com.acme.salarymanagement.employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "employee_code", nullable = false, unique = true, length = 32)
    private String employeeCode;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 320)
    private String email;

    @Column(nullable = false, length = 2)
    private String country;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(name = "job_title", nullable = false, length = 150)
    private String jobTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status", nullable = false, length = 20)
    private EmploymentStatus employmentStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Employee() {
    }

    public static Employee seedEmployee(
            String employeeCode,
            String firstName,
            String lastName,
            String email,
            String country,
            String department,
            String jobTitle,
            EmploymentStatus employmentStatus,
            Instant createdAt) {
        Employee employee = new Employee();
        employee.employeeCode = employeeCode;
        employee.firstName = firstName;
        employee.lastName = lastName;
        employee.email = email;
        employee.country = country;
        employee.department = department;
        employee.jobTitle = jobTitle;
        employee.employmentStatus = employmentStatus;
        employee.createdAt = createdAt;
        employee.updatedAt = createdAt;
        return employee;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = createdAt;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getCountry() {
        return country;
    }

    public String getDepartment() {
        return department;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public EmploymentStatus getEmploymentStatus() {
        return employmentStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
