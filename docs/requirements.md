# Employee Salary Management — Product Requirements Document (PRD)

**Status:** MVP Definition
**Date:** September 22, 2026
**Target Persona:** HR Manager

---

## 1. Goal

Company currently manages compensation data for approximately 10,000 employees across multiple countries using Excel spreadsheets. This makes employee lookups, salary updates, and organization-wide compensation analysis time-consuming and error-prone.

The goal is to provide a secure, centralized web application that enables the HR Manager to manage employee and salary information efficiently and answer common compensation-related questions.

---

## 2. Scope & Core Capabilities

### A. Employee & Salary Management

- Server-side paginated employee directory supporting approximately 10,000 employees.
- Search by employee ID, name, or email.
- Filter by department, country, currency, and employment status.
- Create, edit, and deactivate employee records.
- View employee details and current salary.
- Update salary amount, currency, and effective date.
- Preserve previous salary values as salary history rather than overwriting them.
- Record the date and user associated with salary changes.
- Validate employee and salary data, including valid currency codes and non-negative salary values.

### B. Compensation Insights

The dashboard will provide:

- Employee/headcount distribution by country and department.
- Salary statistics by country, department, and currency.
- Salary-band distribution.
- Minimum, maximum, and average salary within comparable currency/groupings.

Salary amounts will retain their original currency. Where cross-currency comparison is required, a clearly documented static reference-rate table may be used for normalized reporting; normalized values will be treated as reference figures rather than payroll values.

### C. Application Experience

- Responsive employee table and dashboard.
- Clear loading, empty, validation, and error states.
- Server-side pagination, filtering, sorting, and aggregation.
- Basic authenticated access for the HR Manager.

---

## 3. Technical & Performance Requirements

- Automated seed mechanism generating exactly 10,000 deterministic synthetic employee records across multiple countries, departments, currencies, and salary ranges.
- Relational database with appropriate constraints and indexes.
- Backend APIs perform pagination, filtering, and aggregation rather than loading the entire employee dataset into application memory.
- Automated unit and integration tests covering core employee, salary, and analytics behavior.
- Application should remain responsive under the expected MVP data volume.

---

## 4. Deliberately Out of Scope & Rationale

- **Payroll disbursement and tax calculations:** These require country-specific statutory rules and payment processing, which are outside the core salary-management problem.
- **Benefits, bonuses, equity, and full compensation packages:** The MVP focuses on base salary to keep the domain focused and manageable.
- **Employee self-service:** The HR Manager is the defined MVP persona; employee-facing workflows are not required to validate the core product.
- **Enterprise SSO and granular RBAC:** Basic HR Manager authentication is sufficient for the MVP; enterprise identity integration and multiple administrative roles can be added later.
- **Salary approval workflows:** These introduce additional organizational workflow complexity not required for the initial product.
- **Live exchange-rate APIs:** External dependencies can introduce availability and rate-management complexity; a documented static reference-rate approach is sufficient for the MVP.
- **Excel import/export:** The goal is to replace routine spreadsheet-based management with native web workflows rather than reproduce Excel functionality.

---

## 5. Success Criteria

The MVP is successful when an HR Manager can securely:

1. Find and filter employees quickly.
2. Create, edit, and deactivate employee records.
3. Update salaries while preserving salary history.
4. Understand compensation patterns across countries, departments, currencies, and salary bands.
5. Perform these operations reliably with approximately 10,000 employee records.
