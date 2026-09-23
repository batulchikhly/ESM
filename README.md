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
$env:JWT_SECRET = "replace-with-a-long-random-secret-for-local-development"
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

## Current scope

This phase establishes the Spring Boot runtime, environment-based configuration, Flyway/JPA/PostgreSQL wiring, validation and error foundations, CORS, stateless security scaffolding, and application-startup testing. Employee CRUD, salary management, dashboard logic, and authentication business logic are intentionally deferred to later phases.
