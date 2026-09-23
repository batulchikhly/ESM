package com.acme.salarymanagement.repository;

import com.acme.salarymanagement.model.Employee;
import com.acme.salarymanagement.model.EmploymentStatus;
import com.acme.salarymanagement.model.SalaryRecord;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public final class EmployeeSpecifications {

    private EmployeeSpecifications() {
    }

    public static Specification<Employee> search(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String pattern = "%" + value.trim().toLowerCase() + "%";
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("employeeCode")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), pattern));
    }

    public static Specification<Employee> country(String value) {
        return equalsIgnoreCase("country", value);
    }

    public static Specification<Employee> department(String value) {
        return equalsIgnoreCase("department", value);
    }

    public static Specification<Employee> employmentStatus(EmploymentStatus status) {
        if (status == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("employmentStatus"), status);
    }

    public static Specification<Employee> currentSalary(
            String currency,
            BigDecimal minimumSalary,
            BigDecimal maximumSalary,
            LocalDate asOfDate) {
        if (currency == null && minimumSalary == null && maximumSalary == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            Subquery<LocalDate> latestDate = query.subquery(LocalDate.class);
            Root<SalaryRecord> latestSalary = latestDate.from(SalaryRecord.class);
            latestDate.select(criteriaBuilder.greatest(latestSalary.<LocalDate>get("effectiveFrom")));
            latestDate.where(
                    criteriaBuilder.equal(latestSalary.get("employee").get("id"), root.get("id")),
                    criteriaBuilder.lessThanOrEqualTo(latestSalary.get("effectiveFrom"), asOfDate));

            Subquery<Integer> matchingSalary = query.subquery(Integer.class);
            Root<SalaryRecord> salary = matchingSalary.from(SalaryRecord.class);
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(salary.get("employee").get("id"), root.get("id")));
            predicates.add(criteriaBuilder.equal(salary.get("effectiveFrom"), latestDate));
            if (currency != null && !currency.isBlank()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.upper(salary.get("currency")), currency.toUpperCase()));
            }
            if (minimumSalary != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(salary.<BigDecimal>get("annualSalary"), minimumSalary));
            }
            if (maximumSalary != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(salary.<BigDecimal>get("annualSalary"), maximumSalary));
            }
            matchingSalary.select(criteriaBuilder.literal(1));
            matchingSalary.where(predicates);
            return criteriaBuilder.exists(matchingSalary);
        };
    }

    private static Specification<Employee> equalsIgnoreCase(String field, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
                criteriaBuilder.upper(root.get(field)), value.trim().toUpperCase());
    }
}
