package com.acme.salarymanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "salary_records", uniqueConstraints = @UniqueConstraint(
        name = "uq_salary_records_employee_effective_date",
        columnNames = {"employee_id", "effective_from"}))
public class SalaryRecord {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "annual_salary", nullable = false, precision = 19, scale = 4)
    private BigDecimal annualSalary;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    protected SalaryRecord() {
    }

    public static SalaryRecord initialSalary(
            Employee employee,
            BigDecimal annualSalary,
            String currency,
            LocalDate effectiveFrom,
            Instant createdAt,
            User createdBy) {
        SalaryRecord record = new SalaryRecord();
        record.employee = employee;
        record.annualSalary = annualSalary;
        record.currency = currency;
        record.effectiveFrom = effectiveFrom;
        record.createdAt = createdAt;
        record.createdBy = createdBy;
        return record;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public BigDecimal getAnnualSalary() {
        return annualSalary;
    }

    public String getCurrency() {
        return currency;
    }

    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }
}
