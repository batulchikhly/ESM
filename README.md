# ACME Employee Salary Management

Backend foundation for the ACME Employee Salary Management MVP.

## Prerequisites

- Java 21
- Maven 3.9+ (or a Maven wrapper, once generated)
- Docker Desktop for local PostgreSQL

## Configure environment

Copy `.env.example` to `.env` and review the local values. The Spring Boot application reads database, JWT, CORS, and server settings from environment variables. Never commit real secrets.

PowerShell example:

```powershell
Copy-Item .env.example .env
$env:DB_URL = "jdbc:postgresql://localhost:5432/salarymanagement"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "postgres"
$env:JWT_SECRET = "acme-local-jwt-secret-change-before-production-2026-32-bytes-min"
$env:SPRING_PROFILES_ACTIVE = "local"
```

## Start PostgreSQL

From the repository root:

```powershell
docker compose up -d postgres
```

The local database is available at `localhost:5432`. Flyway runs automatically when the backend starts. No domain migration is included in this foundation phase.

## Start the backend

```powershell
cd backend
mvn spring-boot:run
```

The application starts on `http://localhost:8080`. Health is available at `GET http://localhost:8080/actuator/health`.

## Run tests

```powershell
cd backend
mvn test
```

Tests use the `test` profile and an in-memory H2 database, so they do not require PostgreSQL or Docker.

## Generate deterministic demo data

The seed is disabled by default and is intended for local development only. Start PostgreSQL first, then enable it for one application run:

```powershell
$env:APP_SEED_ENABLED = "true"
$env:APP_SEED_RANDOM_SEED = "20260922"
$env:APP_SEED_VERSION = "v1"
$env:APP_SEED_EMPLOYEE_COUNT = "10000"
cd backend
mvn spring-boot:run
```

The runner creates the synthetic HR Manager user `seed-admin@example.test`, exactly 10,000 employees (`EMP00001` through `EMP10000`), and exactly 10,000 initial salary records dated `2025-01-01`. It uses fixed arrays, a fixed random seed, fixed timestamps, and `.example.test` email addresses, so the dataset is reproducible and contains no real personal data.

The `seed_runs` table makes the same seed version idempotent. A repeated run skips insertion. If application data already exists without a matching seed marker, the seed refuses to run rather than deleting or modifying it. Change `APP_SEED_VERSION` only for an explicitly new dataset version. Set `APP_SEED_ENABLED=false` (or omit it) to disable seeding.

To verify the result in PostgreSQL:

```sql
SELECT COUNT(*) FROM employees;
SELECT COUNT(*) FROM salary_records;
SELECT COUNT(*) FROM employees e
JOIN salary_records s ON s.employee_id = e.id;
```

Each query should return `10000` for a clean seeded database.

## Employee API

Employee endpoints are currently open for local development until the authentication phase adds JWT protection. The security boundary is already isolated in `SecurityConfig` and should be changed to require the HR Manager role when authentication is implemented.

List employees with database-side pagination, filtering, search, and allowlisted sorting:

```text
GET /api/employees?page=0&size=25&sort=lastName,asc&search=employee&country=US&department=Engineering&status=ACTIVE&currency=USD&minSalary=50000&maxSalary=180000
```

The response contains only the requested page:

```json
{
  "content": [
    {
      "id": "00000000-0000-0000-0000-000000000001",
      "employeeCode": "EMP00001",
      "firstName": "Maya",
      "lastName": "Bennett",
      "email": "employee00001@example.test",
      "country": "US",
      "department": "Engineering",
      "jobTitle": "Software Engineer",
      "employmentStatus": "ACTIVE",
      "currentSalary": {
        "annualSalary": 100000,
        "currency": "USD",
        "effectiveFrom": "2025-01-01"
      }
    }
  ],
  "page": 0,
  "size": 25,
  "totalElements": 10000,
  "totalPages": 400
}
```

Search is case-insensitive partial matching across employee code, first name, last name, and email. Salary filters apply to the latest salary record effective on or before today; future-dated salary records are excluded from current-salary filtering. Supported sort fields are `employeeCode`, `firstName`, `lastName`, `country`, `department`, `employmentStatus`, and `createdAt`, with `asc` or `desc` direction.

Other endpoints are:

```text
GET   /api/employees/{id}
POST  /api/employees
PUT   /api/employees/{id}
PATCH /api/employees/{id}/deactivate
```

Salary management endpoints preserve every effective-dated record and create an audit row in the same transaction:

```text
POST /api/employees/{id}/salary
GET  /api/employees/{id}/salary-history
```

Example request:

```json
{
  "annualSalary": 75000,
  "currency": "USD",
  "effectiveFrom": "2026-01-01"
}
```

The current salary is the latest record whose effective date is on or before today. Duplicate effective dates return `409 Conflict`. Until authenticated actor propagation is added to the salary service, salary changes use the local `system@example.test` HR user as the audit actor.

## Authentication

Protected employee, salary, and dashboard APIs require a Bearer JWT. Login is available at `POST /api/auth/login`:

```json
{
  "email": "seed-admin@example.test",
  "password": "DemoPassword123!"
}
```

The seeded account is intended for local development and must not be reused in production. JWT signing secrets and expiration are read from `JWT_SECRET` and `JWT_EXPIRATION`; passwords are stored as BCrypt hashes.

## Frontend

The React frontend lives in `frontend/` and uses `VITE_API_BASE_URL` for the backend API URL. See `frontend/README.md` for setup and local authentication instructions.

## Dashboard API

Dashboard endpoints require the HR Manager Bearer JWT and aggregate current salary records in the database:

```text
GET /api/dashboard/summary
GET /api/dashboard/by-country
GET /api/dashboard/by-department
GET /api/dashboard/salary-bands
```

Raw salary statistics stay grouped by currency. Salary bands are local-currency bands (`UNDER_50000`, `50000_TO_99999`, and `100000_OR_MORE`); cross-currency normalized reporting is intentionally deferred until static FX reference data is introduced.

## Current scope

This phase establishes the Spring Boot runtime, environment-based configuration, Flyway/JPA/PostgreSQL wiring, validation and error foundations, CORS, stateless security scaffolding, employee APIs, salary history, and audit transactions. Dashboard logic, JWT authentication business logic, and the React frontend remain deferred.
