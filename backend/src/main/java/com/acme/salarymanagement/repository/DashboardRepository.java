package com.acme.salarymanagement.repository;

import com.acme.salarymanagement.model.SalaryRecord;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface DashboardRepository extends Repository<SalaryRecord, UUID> {

    @Query("""
            select s.currency as currency,
                   count(s.id) as employeeCount,
                   min(s.annualSalary) as minimumSalary,
                   max(s.annualSalary) as maximumSalary,
                   avg(s.annualSalary) as averageSalary
            from SalaryRecord s
            where s.effectiveFrom <= :asOfDate
              and not exists (
                  select newer.id from SalaryRecord newer
                  where newer.employee = s.employee
                    and newer.effectiveFrom <= :asOfDate
                    and newer.effectiveFrom > s.effectiveFrom
              )
            group by s.currency
            order by s.currency
            """)
    List<CurrencySalaryAggregate> salaryStatisticsByCurrency(@Param("asOfDate") LocalDate asOfDate);

    @Query("""
            select s.employee.country as country,
                   s.currency as currency,
                   count(s.id) as employeeCount,
                   min(s.annualSalary) as minimumSalary,
                   max(s.annualSalary) as maximumSalary,
                   avg(s.annualSalary) as averageSalary
            from SalaryRecord s
            where s.effectiveFrom <= :asOfDate
              and not exists (
                  select newer.id from SalaryRecord newer
                  where newer.employee = s.employee
                    and newer.effectiveFrom <= :asOfDate
                    and newer.effectiveFrom > s.effectiveFrom
              )
            group by s.employee.country, s.currency
            order by s.employee.country, s.currency
            """)
    List<CountrySalaryAggregate> salaryStatisticsByCountry(@Param("asOfDate") LocalDate asOfDate);

    @Query("""
            select s.employee.department as department,
                   s.currency as currency,
                   count(s.id) as employeeCount,
                   min(s.annualSalary) as minimumSalary,
                   max(s.annualSalary) as maximumSalary,
                   avg(s.annualSalary) as averageSalary
            from SalaryRecord s
            where s.effectiveFrom <= :asOfDate
              and not exists (
                  select newer.id from SalaryRecord newer
                  where newer.employee = s.employee
                    and newer.effectiveFrom <= :asOfDate
                    and newer.effectiveFrom > s.effectiveFrom
              )
            group by s.employee.department, s.currency
            order by s.employee.department, s.currency
            """)
    List<DepartmentSalaryAggregate> salaryStatisticsByDepartment(@Param("asOfDate") LocalDate asOfDate);

    @Query("""
            select s.currency as currency,
                   case
                     when s.annualSalary < 50000 then 'UNDER_50000'
                     when s.annualSalary < 100000 then '50000_TO_99999'
                     else '100000_OR_MORE'
                   end as band,
                   count(s.id) as employeeCount
            from SalaryRecord s
            where s.effectiveFrom <= :asOfDate
              and not exists (
                  select newer.id from SalaryRecord newer
                  where newer.employee = s.employee
                    and newer.effectiveFrom <= :asOfDate
                    and newer.effectiveFrom > s.effectiveFrom
              )
            group by s.currency,
                     case
                       when s.annualSalary < 50000 then 'UNDER_50000'
                       when s.annualSalary < 100000 then '50000_TO_99999'
                       else '100000_OR_MORE'
                     end
            order by s.currency, band
            """)
    List<SalaryBandAggregate> salaryBands(@Param("asOfDate") LocalDate asOfDate);
}
